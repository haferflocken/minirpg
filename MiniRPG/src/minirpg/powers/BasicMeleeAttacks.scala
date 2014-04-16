package minirpg.powers

import minirpg.model._

object ShortswordAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  override def apply(user : Actor, targets : List[Actor], region : Region) = {
    // TODO
  }
  
  override def canUse(user : Actor) = true;
  
}