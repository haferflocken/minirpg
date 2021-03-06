package minirpg.powers

import minirpg.model._
import minirpg.model.world._
import scalafx.scene.image.Image

object Use extends Power {
  
  val name = "Use";
  val range = 1;
  val cooldown : Long = 0;
  val spriteId = "use";
  val animDuration = minirpg.TENTOTHE9;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    for (t <- targets) {
      t.beUsedBy(user);
    }
    user.state = (Actor.State.UsingPower(this), animDuration);
  }
  
  def canBeUsedBy(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);

  val uiImage = new Image("file:res/sprites/ui/powers/use.png");
  
}