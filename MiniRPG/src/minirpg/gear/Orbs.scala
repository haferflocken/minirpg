package minirpg.gear

import minirpg.model._

abstract class Orb extends Gear {
  val equipSlots = Vector("Hip");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map.empty[String, Int];
}

object RedOrb extends Orb {
  val name = "Red Orb";
  val description = "A magical orb of disintegration whose power can be channeled through a wand.";
}

object YellowOrb extends Orb {
  val name = "Yellow Orb";
  val description = "A magical orb of stunning whose power can be channeled through a wand.";
}

object GreenOrb extends Orb {
  val name = "Green Orb";
  val description = "A magical orb of reflection whose power can be channeled through a wand.";
}

object BlueOrb extends Orb {
  val name = "Blue Orb";
  val description = "A magical orb of absorbtion whose power can be channeled through a wand.";
}

object PurpleOrb extends Orb {
  val name = "Purple Orb";
  val description = "A magical orb of dispelling whose power can be channeled through a wand.";
}