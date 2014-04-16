package minirpg.model

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.Buffer
import scala.collection.immutable.Queue
import minirpg.entities.Corpse
import scala.util.parsing.json.JSONArray
import minirpg.gearMap
import scala.collection.mutable.ArrayBuffer

abstract class Actor(
    val id : String,
    val name : String,
    val equipSlots : Map[String, Int],
    val wieldSlots : Vector[String],
    defaultPowers : Vector[Power],
    baseSkills : Map[String, Int]) extends Entity {
  
  override val useable = true;

  val vitals : LinkedHashMap[String, Int];
  val equipSlotContents = new LinkedHashMap[String, Buffer[Gear]] ++= equipSlots.map((e) => (e._1, new ArrayBuffer[Gear])); 
  val wieldSlotContents = new LinkedHashMap[String, Gear] ++= wieldSlots.map((_, null));
  var equipped : Set[Gear] = Set();
  var powers : Vector[Power] = defaultPowers;
  val skills = new LinkedHashMap[String, Int] ++= baseSkills;
  var path : Queue[(Int, Int)] = null;
  var moveProgress : Long = 0;
  
  /* * * * * * * * * * * * * *
   * Methods.
   * * * * * * * * * * * * * */
  
  override def beUsedBy(user : Entity) : Unit = {
    // TODO
  }
  
  override def tick(delta : Long) : Unit = {
    // Die if any vitals are <= 0.
    for (e <- vitals) {
      if (e._2 <= 0) {
        val corpse = new Corpse(world.makeEntityId, "Corpse of " + name) {
          gear = equipped.toList;
          x = this.x;
          y = this.y;
        };
        world.addEntity(corpse);
        world.removeEntity(this);
        return;
      }
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
  }
  
  // Returns true if the actor has the required slots to equip some gear, or false otherwise.
  def canEquip(g : Gear) : Boolean = g.equipSlots.forall(equipSlotContents.contains(_));
  
  def canWield(g : Gear) : Boolean = g.wieldSlots.forall(wieldSlotContents.contains(_));
  
  // Equip a piece of gear to this actor.
  // Returns a list of the gear that was unequipped to equip the given gear,
  // or null if the gear could not be equipped.
  def equip(g : Gear) : List[Gear] = {
    var out = List[Gear]();
    
    if (!canEquip(g))
      return null;
    
    for (s <- g.equipSlots) {
      val equippedG = equipSlotContents(s);
      if (equippedG.size >= equipSlots(s)) {
        out = equippedG(0) :: out;
        unequipNoUpdate(equippedG(0));
      }
      equipSlotContents(s).append(g);
    };
    
    initEquipped;
    initPowers;
    initSkills;
    
    return out;
  }
  
  // Unequip a piece of gear from this actor.
  // If called with a piece of gear the actor isn't wearing, it'll mess things up.
  def unequip(g : Gear) = {
    unequipNoUpdate(g);
    initEquipped;
    initPowers;
    initSkills;
  }
  
  // Tell the actor to move to a coordinate.
  def setMoveTarget(targetX : Int, targetY : Int) = {
    path = world.findPath(x, y, targetX, targetY);
    if (path != null && path.length == 0)
      path = null;
  }
  
  
  /* * * * * * * * * * * * * *
   * Helper methods.
   * * * * * * * * * * * * * */
  
  private def unequipNoUpdate(g : Gear) : Unit = {
    for (s <- g.equipSlots) {
      equipSlotContents(s) -= g;
    }
    val p = world.getSpotNextTo(x, y);
    world.addEntity(new GearEntity(world.makeEntityId, g) {
      x = p._1;
      y = p._2;
    });
  }
  
  private def initEquipped = {
    val equippedBuffer = new ArrayBuffer[Gear];
    for (b <- equipSlotContents.values) {
      equippedBuffer.appendAll(b);
    }
    equipped = equippedBuffer.toSet;
  }
  
  private def initPowers = {
    powers = defaultPowers;
    for (g <- equipped if g.powers != null if g.wieldSlots == null) {
      powers = powers ++ g.powers;
    }
    for (g <- wieldSlotContents.values) {
      powers = powers ++ g.powers;
    }
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
  }
  
}