package minirpg.gear

import minirpg.model._

object Longsword extends Gear {

  override def slots = Array("Main Hand");
  override def powers = Array(LongswordAttack);
    
}

private object LongswordAttack extends Power {
  
  override def name = "Attack";
  override def range = 1;
  
  override def apply(user : Actor, targets : List[Actor], region : Region) = {
    // TODO
  }
  
}