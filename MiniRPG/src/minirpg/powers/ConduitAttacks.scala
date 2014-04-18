package minirpg.powers

import minirpg.model._

object ExplosiveConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.rectangle(cX, cY, 3, 3);
  
}

object FocusedConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}

object TrapConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}