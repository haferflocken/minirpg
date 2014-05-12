package minirpg.powers

import minirpg.model._
import minirpg.model.world._

object Use extends Power {
  
  val name = "Use";
  val range = 2;
  val cooldown : Long = 0;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    for (t <- targets) {
      t.beUsedBy(user);
    }
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);

}