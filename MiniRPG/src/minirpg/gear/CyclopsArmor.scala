package minirpg.gear

import minirpg.model._
import minirpg.model.world.Gear
import scalafx.scene.image.Image

object CyclopsGoggles extends Gear {

  val name = "Cyclops' Goggles";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Head");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 2, Skills.slashRes -> 2, Skills.threat -> 2);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
  
}

object CyclopsJacket extends Gear {

  val name = "Cyclops' Jacket";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Torso");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 4, Skills.slashRes -> 4, Skills.bashRes -> 1, Skills.threat -> 3);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object CyclopsPants extends Gear {

  val name = "Cyclops' Pants";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Legs");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 4, Skills.slashRes -> 4, Skills.bashRes -> 1, Skills.threat -> 3);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object CyclopsGloves extends Gear {

  val name = "Cyclops' Gloves";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Hands");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 2, Skills.slashRes -> 2, Skills.threat -> 2);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object CyclopsBoots extends Gear {

  val name = "Frog's Boots";
  val description = "Made by the legendary tailor and inventor, Cyclops.";
  val equipSlots = Vector("Feet");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 2, Skills.slashRes -> 2, Skills.threat -> 2);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}