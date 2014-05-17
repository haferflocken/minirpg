package minirpg.gear

import minirpg.model._
import minirpg.model.world.Gear

object CyclopsGoggles extends Gear {

  val name = "Cyclops' Goggles";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Head");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 2, Skills.slashRes -> 2);
  
}

object CyclopsJacket extends Gear {

  val name = "Cyclops' Jacket";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Torso");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 4, Skills.slashRes -> 4, Skills.bashRes -> 1);
    
}

object CyclopsPants extends Gear {

  val name = "Cyclops' Pants";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Legs");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 4, Skills.slashRes -> 4, Skills.bashRes -> 1);
    
}

object CyclopsGloves extends Gear {

  val name = "Cyclops' Gloves";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Hands");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 2, Skills.slashRes -> 2);
    
}

object CyclopsBoots extends Gear {

  val name = "Frog's Boots";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Feet");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 2, Skills.slashRes -> 2);
    
}