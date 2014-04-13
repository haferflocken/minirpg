package minirpg.gear

import minirpg.model._

object Shortsword extends Gear {

  val name = "Shortsword";
  val slots = Array("Main Hand");
  val powers = Array[Power](ShortswordAttack);
  val skillBonuses = Map.empty[String, Int];
  
  def makeEntity = null;
    
}

private object ShortswordAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  override def apply(user : Actor, targets : List[Actor], region : Region) = {
    // TODO
  }
  
  override def canUse(user : Actor) = true;
  
}