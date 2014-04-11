package minirpg.gear

import minirpg.model._

object Longsword extends Gear {

  val slots = Array("Main Hand");
  val powers = Array[Power](LongswordAttack);
    
}

private object LongswordAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  override def apply(user : Actor, targets : List[Actor], region : Region) = {
    // TODO
  }
  
}