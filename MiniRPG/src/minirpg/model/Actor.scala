package minirpg.model

import scala.collection.mutable.LinkedHashMap
import scala.collection.immutable.Queue

abstract class Actor(val id : String, val name : String, val slotNames : Array[String], defaultPowers : Vector[Power]) extends Entity {

  val vitals : LinkedHashMap[String, Int];
  val slotContents = new LinkedHashMap[String, Gear] ++= slotNames.map((_, null)); 
  var equipped : Set[Gear] = Set();
  var powers : Vector[Power] = defaultPowers.toVector;
  val skills = Skills.makeZeroMap;
  var path : Queue[(Int, Int)] = null;
  
  /* * * * * * * * * * * * * *
   * Methods.
   * * * * * * * * * * * * * */
  
  def beUsed(user : Entity) : Unit = {
    // TODO
  }
  
  def tick : Unit = {
    // Die if any vitals are <= 0.
    for (e <- vitals) {
      if (e._2 <= 0) {
        // TODO Die.
      }
    }
    
    // Move along the path.
    if (path != null) {
      val next = path.dequeue;
      x = next._1._1;
      y = next._1._2;
      path = next._2;
      if (path.length == 0)
        path = null;
    }
  }
  
  // Equip a piece of gear to this actor.
  // Returns a list of the gear that was unequipped to equip the given gear.
  def equip(g : Gear) : List[Gear] = {
    var out = List[Gear]();
    
    g.slots.foreach(s => {
      val equippedG = slotContents(s);
      if (equippedG != null)
        out = equippedG :: out;
      unequipNoUpdate(equippedG);
      slotContents(s) = g;
    });
    
    initPowers;
    initEquipped;
    initSkills;
    
    return out;
  }
  
  // Unequip a piece of gear from this actor.
  // If called with a piece of gear the actor isn't wearing, it'll mess things up.
  def unequip(g : Gear) = {
    unequipNoUpdate(g);
    initPowers;
    initEquipped;
    initSkills;
  }
  
  // Tell the actor to move to a coordinate.
  def setMoveTarget(targetX : Int, targetY : Int) = {
    path = world.findPath(x, y, targetX, targetY);
  }
  
  
  /* * * * * * * * * * * * * *
   * Helper methods.
   * * * * * * * * * * * * * */
  
  private def unequipNoUpdate(g : Gear) = {
    g.slots.foreach(s => {
      slotContents(s) = null;
    });
  }
  
  private def initPowers = {
    powers = slotContents.foldLeft(defaultPowers)((z : Vector[Power], p : (String, Gear)) => {
      if (p._2 == null) z
      else z ++ p._2.powers
    });
  }
  
  private def initEquipped = {
    equipped = slotContents.values.toSet;
  }
  
  private def initSkills : Unit = {
    skills ++= Skills.zeroTuples;
    for (g <- equipped) {
      for (b <- g.skillBonuses) {
        val level = skills.get(b._1);
        if (level.nonEmpty)
          skills.update(b._1, b._2 + level.get);
      }
    }
  }
}