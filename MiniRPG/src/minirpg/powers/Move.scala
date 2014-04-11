package minirpg.powers

import minirpg.model._

object Move extends Power {
  
  val name = "Move";
  val range = 64;
  
  override def apply(user : Actor, targets : List[Actor], region : Region) = {
    user.setMoveTarget(region.centerX, region.centerY);
  }

}