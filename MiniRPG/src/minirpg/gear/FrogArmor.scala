package minirpg.gear

import minirpg.model._
import minirpg.model.world.Gear

object FrogHelmet extends Gear {

  val name = "Frog's Helmet";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Head");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.impactRes -> 6);
    
}

object FrogCuirass extends Gear {

  val name = "Frog's Cuirass";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Torso");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.impactRes -> 11);
    
}

object FrogGreaves extends Gear {

  val name = "Frog's Greaves";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Legs");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.impactRes -> 8);
    
}

object FrogGauntlets extends Gear {

  val name = "Frog's Gauntlets";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Hands");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.impactRes -> 4);
    
}

object FrogBoots extends Gear {

  val name = "Frog's Boots";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Feet");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.impactRes -> 4);
    
}