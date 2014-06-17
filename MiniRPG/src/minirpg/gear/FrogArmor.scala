package minirpg.gear

import minirpg.model._
import minirpg.model.world.Gear
import scalafx.scene.image.Image

object FrogHelmet extends Gear {

  val name = "Frog's Helmet";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Head");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 6, Skills.slashRes -> 6, Skills.bashRes -> 4, Skills.threat -> 3);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object FrogCuirass extends Gear {

  val name = "Frog's Cuirass";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Torso");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 11, Skills.slashRes -> 11, Skills.bashRes -> 9, Skills.threat -> 4);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object FrogGreaves extends Gear {

  val name = "Frog's Greaves";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Legs");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 8, Skills.slashRes -> 8, Skills.bashRes -> 6, Skills.threat -> 4);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object FrogGauntlets extends Gear {

  val name = "Frog's Gauntlets";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Hands");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 4, Skills.slashRes -> 4, Skills.bashRes -> 2, Skills.threat -> 3);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}

object FrogBoots extends Gear {

  val name = "Frog's Boots";
  val description = "Made by the legendary blacksmith, Frog.";
  val equipSlots = Vector("Feet");
  val wieldSlots = null;
  val powers = null;
  val skillBonuses = Map(Skills.pierceRes -> 4, Skills.slashRes -> 4, Skills.bashRes -> 2, Skills.threat -> 3);
  val uiWieldImage = Gear.uiWieldImage;
  val uiUnwieldImage = Gear.uiUnwieldImage;
    
}