package minirpg.powers

import minirpg.model._

object ShortswordAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}