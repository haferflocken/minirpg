package minirpg.powers

import minirpg.model._

object Move extends Power {
  
  val name = "Move";
  val range = 64;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    user.setMoveTarget(region.centerX, region.centerY);
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);

}