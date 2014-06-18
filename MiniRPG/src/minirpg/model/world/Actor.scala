package minirpg.model.world

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.Buffer
import scala.collection.immutable.Queue
import scala.util.parsing.json.JSONArray
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Publisher
import minirpg.gearMap
import minirpg.actorAIMap
import minirpg.model._
import minirpg.model.world._
import minirpg.entities._

abstract class Actor(
    val id : String,
    val name : String,
    val equipSlots : Map[String, Int],
    val wieldSlots : Vector[String],
    defaultPowers : Vector[Power],
    baseSkills : Map[String, Int],
    val brain : ActorAI) extends Entity with Publisher[ActorEvent] {
  
  type Pub = Actor;
  override val useable = true;

  val vitals : LinkedHashMap[String, (Int, Int)]; // A map of names to (current, max)
  val equipSlotContents = new LinkedHashMap[String, Buffer[Gear]] ++= equipSlots.map((e) => (e._1, new ArrayBuffer[Gear])); 
  val wieldSlotContents = new LinkedHashMap[String, Gear] ++= wieldSlots.map((_, null));
  val equipped : Buffer[Gear] = new ArrayBuffer;
  var powers : Vector[Power] = defaultPowers;
  val powerUseables = new LinkedHashMap[Power, Boolean] ++= powers.map(p => (p, p.canUse(this)));
  val powerCooldowns = new LinkedHashMap[Power, Long] ++= powers.map((_, 0.longValue));
  val skills = new LinkedHashMap[String, Int] ++= baseSkills;
  var path : Queue[(Int, Int)] = null;
  var moveProgress : Long = 0;
 
  /* * * * * * * * * * * * * *
   * Methods.
   * * * * * * * * * * * * * */
  
  override def beUsedBy(user : Entity) : Unit = {
    // TODO
    publish(ActorEvent(this, ActorEvent.BE_USED));
  }
  
  override def tick(delta : Long) : Unit = {
    // Die if any vitals are <= 0.
    for (e <- vitals) {
      if (e._2._1 <= 0) {
        val corpse = new Corpse(world.makeEntityId, "Corpse of " + name) {
          gear = equipped.toList;
          x = this.x;
          y = this.y;
        };
        world.addEntity(corpse);
        world.removeEntity(this);
        publish(ActorEvent(this, ActorEvent.DIE));
        return;
      }
    }
    
    // Check which powers are useable.
    for ((p, u) <- powerUseables) {
      val canUse = p.canUse(this);
      powerUseables(p) = canUse;
      if (u && !canUse)
        publish(ActorEvent(this, ActorEvent.POWER_NO_LONGER_USEABLE));
      else if (!u && canUse)
        publish(ActorEvent(this, ActorEvent.POWER_NOW_USEABLE));
    }
    
    // Cool powers off.
    for ((p, c) <- powerCooldowns) {
      if (powerUseables(p)) {
        if (c > 0) powerCooldowns(p) = c - delta max 0;
      }
      else if (c != p.cooldown) powerCooldowns(p) = p.cooldown;
    }
    
    // Move along the path.
    if (path != null) {
      val speed = skills(Skills.speed);
      moveProgress += (speed * delta).longValue;
      if (moveProgress >= minirpg.TENTOTHE11) {
        val next = path.dequeue;
        x = next._1._1;
        y = next._1._2;
        path = next._2;
        if (path.length == 0)
          path = null;
        moveProgress = 0;
      }
    }
    
    // Think about life.
    if (brain != null)
      brain.tick(this, delta);
  }
  
  // Change the value of a vital.
  def modVital(vital : String, amount : Int) : Unit = {
    val current = vitals(vital);
    vitals(vital) = (current._1 + amount, current._2);
  }
  
  // Returns true if the actor has the required slots to equip some gear, or false otherwise.
  def canEquip(g : Gear) : Boolean = g.equipSlots.forall(equipSlotContents.contains(_));
  
  // Returns true if the actor has a given gear equipped, false otherwise.
  def isEquipped(g : Gear) : Boolean = {
    return equipped contains g;
  }
  
  // Equip a piece of gear to this actor.
  // Returns a list of the gear that was unequipped to equip the given gear,
  // or null if the gear could not be equipped.
  def equip(g : Gear) : List[Gear] = {
    if (!canEquip(g))
      return null;
    
    var out = List[Gear]();
    
    for (s <- g.equipSlots) {
      val equippedG = equipSlotContents(s);
      if (equippedG.size >= equipSlots(s)) {
        out = equippedG(0) :: out;
        unequipNoUpdate(equippedG(0));
      }
      equipSlotContents(s).append(g);
    };
    equipped += g;
    
    //initEquipped;
    initPowers;
    initSkills;
    publish(ActorEvent(this, ActorEvent.EQUIP));
    
    return out;
  }
  
  // Unequip a piece of gear from this actor.
  // If called with a piece of gear the actor isn't wearing, it'll mess things up.
  def unequip(g : Gear) : Unit = {
    if (!isEquipped(g))
      return;
    
    unequipNoUpdate(g);
    initPowers;
    initSkills;
    publish(ActorEvent(this, ActorEvent.UNEQUIP));
  }
  
  // Returns true if the actor has the wield slots to wield a gear, false otherwise.
  def canWield(g : Gear) : Boolean = g.wieldSlots != null && g.wieldSlots.forall(wieldSlotContents.contains(_));
  
  // Returns true if the actor is wielding a gear.
  def isWielding(g : Gear) : Boolean = {
    for (s <- wieldSlotContents if s._2 == g)
      return true;
    return false;
  }
  
  // Have this actor wield a piece of gear.
  def wield(g : Gear) : Unit = {
    if (!equipped.contains(g) || !canWield(g))
      return;
    
    for (s <- g.wieldSlots) {
      val wieldedG = wieldSlotContents(s);
      if (wieldedG != null)
        unwieldNoUpdate(wieldedG);
      wieldSlotContents(s) = g;
    }
    
    initPowers;
    publish(ActorEvent(this, ActorEvent.WIELD));
  }
  
  // Have this actor unwield a piece of gear.
  def unwield(g : Gear) : Unit = {
    if (!isWielding(g))
      return;
    
    unwieldNoUpdate(g);
    initPowers;
    publish(ActorEvent(this, ActorEvent.UNWIELD));
  }
  
  // Tell the actor to move to a coordinate.
  def setMoveTarget(targetX : Int, targetY : Int) = {
    path = world.findPath(x, y, targetX, targetY);
    if (path != null && path.length == 0)
      path = null;
    publish(ActorEvent(this, ActorEvent.MOVE_TARGET_SET));
  }
  
  
  /* * * * * * * * * * * * * *
   * Helper methods.
   * * * * * * * * * * * * * */
  
  private def unequipNoUpdate(g : Gear) : Unit = {
    for (s <- g.equipSlots) {
      equipSlotContents(s) -= g;
    }
    equipped -= g;
    val p = world.getSpotNextTo(x, y);
    world.addEntity(new GearEntity(world.makeEntityId, g) {
      x = p._1;
      y = p._2;
    });
  }
  
  private def unwieldNoUpdate(g : Gear) : Unit = {
    for (s <- g.wieldSlots) {
      wieldSlotContents(s) = null;
    }
  }
  
  private def initPowers = {
    // Make the powers vector.
    powers = defaultPowers;
    for (g <- equipped if g.powers != null if g.wieldSlots == null) {
      powers = powers ++ g.powers;
    }
    for (g <- wieldSlotContents.values.toSet if g != null) {
      powers = powers ++ g.powers;
    }
    
    // Update the usability map.
    powerUseables.clear;
    powerUseables ++= powers.map(p => (p, p.canUse(this)));
    
    // Update the cooldowns map.
    for ((p, c) <- powerCooldowns) {
      if (!powers.contains(p))
        powerCooldowns -= p;
    }
    powerCooldowns ++= powers.filter(!powerCooldowns.contains(_)).map(p => (p, p.cooldown));
  }
  
  private def initSkills : Unit = {
    skills ++= baseSkills;
    for (g <- equipped) {
      for (b <- g.skillBonuses) {
        val level = skills.get(b._1);
        if (level.nonEmpty)
          skills.update(b._1, b._2 + level.get);
      }
    }
  }
}

trait ActorEvent {
  val actor : Actor;
  val event : String;
}

object ActorEvent {
  val BE_USED = "be used";
  val EQUIP = "equip";
  val UNEQUIP = "unequip";
  val WIELD = "wield";
  val UNWIELD = "unwield";
  val MOVE_TARGET_SET = "move target set";
  val DIE = "die";
  val VITALS_CHANGED = "vitals changed";
  val POWER_NOW_USEABLE = "power now useable";
  val POWER_NO_LONGER_USEABLE = "power no longer useable";
    
  def apply(a : Actor, e : String) : ActorEvent = {
    return new ActorEvent {
      val actor = a;
      val event = e;
    }
  }
}

abstract class ActorBuilder[A <: Actor] extends EntityBuilder[A] {
  
  def extractName(args : Map[String, Any]) : String = extract[String]("name", args, null);
  
  def extractGear(args : Map[String, Any]) : List[Gear] = {
    val jsonGear = extract[JSONArray]("gear", args, null);
    if (jsonGear == null)
      return null;
    var out : List[Gear] = Nil;
    for (v <- jsonGear.list) {
      if (v == null)
        println("Gear in an actor cannot be null.");
      else if (!v.isInstanceOf[String])
        println("Gear names must be strings.");
      else {
        val gString = v.asInstanceOf[String];
        val g = gearMap.getOrElse(gString, null);
        if (g == null)
          println("Failed to find gear of type \"" + gString + "\".");
        else {
          out = g :: out;
        }
      }
    }
    return out;
  };
  
  def extractBrain(args : Map[String, Any]) : ActorAI = {
    val brainType = extract[String]("brain", args, null);
    if (brainType == null)
      return null;
    return actorAIMap.getOrElse(brainType, null);
  };
  
  
}