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
import minirpg.ui.SpriteView
import minirpg.TENTOTHE9 

abstract class Actor(
    val id : String,
    val name : String,
    val equipSlots : Map[String, Int],
    val equipSlotOrder : Vector[String],
    val wieldSlots : Vector[String],
    defaultPowers : Vector[Power],
    baseSkills : Map[String, Int],
    val brain : ActorAI) extends Entity with Publisher[Actor.Event] {
  
  type Pub = Actor;
  override val useable = true;

  val vitals : LinkedHashMap[String, (Int, Int)]; // A map of names to (current, max)
  val equipSlotContents = new LinkedHashMap[String, Buffer[Gear]] ++= equipSlots.map((e) => (e._1, new ArrayBuffer[Gear])); 
  val wieldSlotContents = new LinkedHashMap[String, Gear] ++= wieldSlots.map((_, null));
  val equipped : Buffer[Gear] = new ArrayBuffer;
  var powers : Vector[Power] = defaultPowers;
  val powerUseables = new LinkedHashMap[Power, Boolean] ++= powers.map(p => (p, p.canBeUsedBy(this)));
  val powerCooldowns = new LinkedHashMap[Power, Long] ++= powers.map((_, 0.longValue));
  val skills = new LinkedHashMap[String, Int] ++= baseSkills;
  protected var path : Queue[(Int, Int)] = null;
  protected var moveProgress : Long = 0;
  private var _dir : (Int, Int) = (0, 0);
  private var _state : Actor.State = Actor.State.Idle;
  private var _stateTimer : Long = 0;
  
  val spriteView : SpriteView;
  val sprites : Map[String, Sprite];
 
  /* * * * * * * * * * * * * *
   * Methods.
   * * * * * * * * * * * * * */
  
  override def beUsedBy(user : Entity) : Unit = {
    // TODO
    publish(Actor.Event.BeUsedBy(user));
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
        publish(Actor.Event.Die);
        return;
      }
    }
    
    // Check which powers are useable.
    for ((p, u) <- powerUseables) {
      val canUse = p.canBeUsedBy(this);
      powerUseables(p) = canUse;
      if (u && !canUse)
        publish(Actor.Event.PowerNowNotUseable(p));
      else if (!u && canUse)
        publish(Actor.Event.PowerNowUseable(p));
    }
    
    // Cool powers off.
    for ((p, c) <- powerCooldowns) {
      if (powerUseables(p)) {
        if (c > 0) powerCooldowns(p) = c - delta max 0;
      }
      else if (c != p.cooldown) powerCooldowns(p) = p.cooldown;
    }
    
    // Move and think when not stunned.
    if (_state != Actor.State.Stunned) {
      // Move along the path.
      if (path != null) {
        val speed = skills(Skills.speed);
        moveProgress += (speed * delta).toLong;
        val (tileWidth, tileHeight) = (world.tileGrid.tileWidth, world.tileGrid.tileHeight);
        node.layoutX = x * tileWidth + nodeOffsetX + tileWidth * dir._1 * moveProgress / minirpg.TENTOTHE11;
        node.layoutY = y * tileHeight + nodeOffsetY + tileHeight * dir._2 * moveProgress / minirpg.TENTOTHE11;
        
        if (moveProgress >= minirpg.TENTOTHE11) {
          val next = path.dequeue;
          x = next._1._1;
          y = next._1._2;
          path = next._2;
          if (path.length == 0) {
            path = null;
            dir = (0, 0);
          }
          else {
            val (nX, nY) = path.front;
            dir = (nX - x, nY - y);
          }
          moveProgress = 0;
        }
      }
      
      // Think about life.
      if (brain != null)
        brain.tick(this, delta);
    }
    
    // Progress the state.
    if (_stateTimer > 0) {
      _stateTimer -= delta;
      if (_stateTimer <= 0) {
        _stateTimer = 0;
        _state = Actor.State.Idle;
        spriteView.sprite = sprites("idle");
      }
    }
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
    publish(Actor.Event.Equip(g));
    
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
    publish(Actor.Event.Unequip(g));
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
    publish(Actor.Event.Wield(g));
  }
  
  // Have this actor unwield a piece of gear.
  def unwield(g : Gear) : Unit = {
    if (!isWielding(g))
      return;
    
    unwieldNoUpdate(g);
    initPowers;
    publish(Actor.Event.Unwield(g));
  }
  
  // Tell the actor to move to a coordinate.
  def setMoveTarget(targetX : Int, targetY : Int) = {
    path = world.findPath(x, y, targetX, targetY);
    if (path == null || path.length == 0) {
      path = null;
      dir = (0, 0);
    }
    else if (path != null) {
      val (nX, nY) = path.front;
      dir = (nX - x, nY - y);
    }
    publish(Actor.Event.MoveTargetSet(targetX, targetY));
  }
  
  // Check if we have a path.
  def hasPath = path != null;
  
  // Set the direction we're going.
  def dir_=(o : (Int, Int)) : Unit = {
    if (o == (0, 0)) {
      spriteView.sprite = sprites("idle");
    }
    else {
      spriteView.sprite = sprites("walk");
      o match {
        case (1, 0) => spriteView.rotate = 90;
        case (-1, 0) => spriteView.rotate = 270;
        case (0, 1) => spriteView.rotate = 180;
        case (0, -1) => spriteView.rotate = 0;
        case _ => spriteView.rotate = 0;
      };
    }
    if (o != _dir || o == (0, 0)) {
      moveProgress = 0;
      x = x;
      y = y;
    }
    _dir = o;
  };
  
  // Get the direction we're going.
  def dir = _dir;
  
  // Get the state.
  def state = _state;
  def stateTimer = _stateTimer;
  
  def state_=(tempState : (Actor.State, Long)) : Unit = {
    val (newState, duration) = tempState;
    _state = newState;
    _stateTimer = duration;
    
    val sprite = sprites.getOrElse(newState.spriteId, null);
    if (sprite != null)
      spriteView.sprite = sprite;
    else
      spriteView.sprite = sprites("idle");
  };
  
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
    powerUseables ++= powers.map(p => (p, p.canBeUsedBy(this)));
    
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


object Actor {
  abstract class State(val name : String, val spriteId : String);
  object State {
    case object Idle extends State("idle", "idle");
    case class UsingPower(val power : Power) extends State("using power", power.spriteId);
    case object Stunned extends State("stunned", "stunned");
  }
  
  abstract class Event(val eventName : String);
  object Event {
    case class BeUsedBy(val by : Entity) extends Event("be used");
    case class Equip(val gear : Gear) extends Event("equip");
    case class Unequip(val gear : Gear) extends Event("unequip");
    case class Wield(val gear : Gear) extends Event("wield");
    case class Unwield(val gear : Gear) extends Event("unwield");
    case class MoveTargetSet(val targetX : Int, val targetY : Int) extends Event("move target set");
    case object Die extends Event("die");
    case object VitalsChanged extends Event("vitals changed");
    case class PowerNowUseable(val power : Power) extends Event("power now useable");
    case class PowerNowNotUseable(val power : Power) extends Event("power now not useable");
  }
}


abstract class ActorBuilder[A <: Actor] extends EntityBuilder[A] {
  
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