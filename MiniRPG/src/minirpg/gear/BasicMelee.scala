package minirpg.gear

import minirpg.model._
import minirpg.powers._

object Shortsword extends Gear {

  val name = "Shortsword";
  val description = "Sharp enough to be effective.";
  val equipSlots = Vector("Hip");
  val wieldSlots = Vector("Main Hand");
  val powers = Vector[Power](ShortswordAttack);
  val skillBonuses = Map.empty[String, Int];
    
}