package minirpg.gear

import minirpg.model._
import minirpg.model.world.Gear
import scalafx.scene.image.Image

abstract class Orb extends Gear {
  val equipSlots = Vector("Hip");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map.empty[String, Int];
  val uiWieldImage = Gear.uiImage;
  val uiUnwieldImage = Gear.uiImage;
}

object CyanOrb extends Orb {
  val name = "Cyan Orb";
  val description = "Cyan magic weakens other magic.";
}

object MagentaOrb extends Orb {
  val name = "Magenta Orb";
  val description = "Magenta magic calms spirits down.";
}

object YellowOrb extends Orb {
  val name = "Yellow Orb";
  val description = "Yellow magic scares spirits.";
}


object BlackOrb extends Orb {
  val name = "Black Orb";
  val description = "Black magic disintegrates matter.";
}
