package minirpg.gear

import minirpg.model._
import minirpg.powers._

abstract class Conduit extends Gear {
  val equipSlots = Vector("Back");
  val wieldSlots = Vector("Main Hand", "Off Hand");
  val skillBonuses = Map.empty[String, Int];
}

object ExplosiveConduit extends Conduit {
  val name = "Explosive Conduit";
  val description = "Conducts magical energy into an explosion.";
  val powers = Vector[Power](RedExplosiveConduitAttack);
}

object FocusedConduit extends Conduit {
  val name = "Focused Conduit";
  val description = "Conducts magical energy to a single point.";
  val powers = Vector[Power](RedFocusedConduitAttack);
}

object TrapConduit extends Conduit {
  val name = "Trap Conduit";
  val description = "Conducts magical energy to create a proximity mine.";
  val powers = Vector[Power](RedTrapConduitAttack);
}
