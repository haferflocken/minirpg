package minirpg.gear

import minirpg.model._
import minirpg.powers._
import minirpg.model.world.Power
import minirpg.model.world.Gear

object ConduitBlaster extends Gear {
  val name = "Conduit Blaster";
  val description = "Conducts magical energy into an explosion.";
  val equipSlots = Vector("Back");
  val wieldSlots = Vector("Main Hand", "Off Hand");
  val powers = Vector[Power](CyanConduitBlasterAttack);
  val skillBonuses = Map(Skills.threat -> 10);
}

object ConduitRifle extends Gear {
  val name = "Conduit Rifle";
  val description = "Conducts magical energy to a single point.";
  val equipSlots = Vector("Back");
  val wieldSlots = Vector("Main Hand", "Off Hand");
  val powers = Vector[Power](CyanConduitRifleAttack);
  val skillBonuses = Map(Skills.threat -> 15);
}

object ConduitMineLauncher extends Gear {
  val name = "Conduit Mine Launcher";
  val description = "Conducts magical energy to create a proximity mine.";
  val equipSlots = Vector("Back");
  val wieldSlots = Vector("Main Hand", "Off Hand");
  val powers = Vector[Power](CyanConduitMineLauncherAttack);
  val skillBonuses = Map(Skills.threat -> 10);
}
