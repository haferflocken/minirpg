package minirpg.model

import scala.collection.mutable.LinkedHashMap
import scala.collection.immutable.Queue
import minirpg.entities.Corpse
import scala.util.parsing.json.JSONArray
import minirpg.gearMap

abstract class Actor(
    val id : String,
    val name : String,
    val slotNames : Array[String],
    defaultPowers : Vector[Power],
    baseSkills : Map[String, Int]) extends Entity {
  
  override val useable = true;

  val vitals : LinkedHashMap[String, Int];
  val slotContents = new LinkedHashMap[String, Gear] ++= slotNames.map((_, null)); 
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
  
  // Equip a piece of gear to this actor.
  // Returns a list of the gear that was unequipped to equip the given gear.
  def equip(g : Gear) : List[Gear] = {
    var out = List[Gear]();
    
    g.slots.foreach(s => {
      val equippedG = slotContents(s);
      if (equippedG != null) {
        out = equippedG :: out;
        unequipNoUpdate(equippedG);
      }
      slotContents(s) = g;
    });
    
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
  }
  
  
  /* * * * * * * * * * * * * *
   * Helper methods.
   * * * * * * * * * * * * * */
  
  private def unequipNoUpdate(g : Gear) : Unit = {
    for (s <- g.slots) {
      slotContents(s) = null;
    }
    val p = world.getSpotNextTo(x, y);
    world.addEntity(new GearEntity(world.makeEntityId, g) {
      x = p._1;
      y = p._2;
    });
  }
  
  private def initEquipped = {
    equipped = slotContents.values.toSet.filter(_ != null);
  }
  
  private def initPowers = {
    powers = defaultPowers;
    for (g <- equipped if g.powers != null) {
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