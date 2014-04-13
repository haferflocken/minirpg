package minirpg.gear

import minirpg.model._

object Shortsword extends Gear {

  val name = "Shortsword";
  val description = "Sharp enough to be effective.";
  val slots = Vector("Main Hand");
  val powers = Vector[Power](ShortswordAttack);
  val skillBonuses = Map.empty[String, Int];
    
}

private object ShortswordAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  override def apply(user : Actor, targets : List[Actor], region : Region) = {
    // TODO
  }
  
  override def canUse(user : Actor) = true;
  
}