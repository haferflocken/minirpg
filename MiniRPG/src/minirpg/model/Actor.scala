package minirpg.model

import scala.collection.mutable.LinkedHashMap
import scala.collection.immutable.Queue
import minirpg.util.Pathfinder

class Actor(iName : String, slotNames : Array[String], defaultPowers : List[Power]) extends Entity {

  val slotContents = new LinkedHashMap[String, Gear] ++ slotNames.map(s => (s, null));
  var equipped : Set[Gear] = Set();
  var powers : Array[Power] = defaultPowers.toArray;
  var path : Queue[(Int, Int)] = null;
  var world : World = null;
  
  /* * * * * * * * * * * * * *
   * Getters.
   * * * * * * * * * * * * * */
  def name = iName;
  
  /* * * * * * * * * * * * * *
   * Methods.
   * * * * * * * * * * * * * */
  
  def tick : Unit = {
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
    
    return out;
  }
  
  // Unequip a piece of gear from this actor.
  // If called with a piece of gear the actor isn't wearing, it'll mess things up.
  def unequip(g : Gear) = {
    unequipNoUpdate(g);
    initPowers;
    initEquipped;
  }
  
  // Tell the actor to move to a coordinate.
  def setMoveTarget(targetX : Int, targetY : Int) = {
    path = Pathfinder.findPath(x, y, targetX, targetY, world);
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
    powers = slotContents.foldLeft(defaultPowers)((z : List[Power], p : (String, Gear)) => {
      if (p._2 == null)
        z;
      else
        z ++ p._2.powers;
    }).toArray;
  }
  
  private def initEquipped = {
    equipped = slotContents.values.toSet;
  }
}