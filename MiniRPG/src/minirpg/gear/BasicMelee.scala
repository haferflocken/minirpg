package minirpg.gear

import minirpg.model._
import minirpg.powers._
import minirpg.model.world.Power
import minirpg.model.world.Gear
import scalafx.scene.image.Image

object Shortsword extends Gear {

  val name = "Shortsword";
  val description = "Sharp enough to be effective.";
  val equipSlots = Vector("Hip");
  val wieldSlots = Vector("Main Hand");
  val powers = Vector[Power](ShortswordAttack);
  val skillBonuses = Map(Skills.threat -> 5);
  
  val uiWieldImage = new Image("file:res/sprites/ui/gear/sword-wield.png");
  val uiUnwieldImage = new Image("file:res/sprites/ui/gear/sword-unwield.png");
    
}
