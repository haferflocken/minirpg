package minirpg.model.world

import minirpg.model._
import scalafx.scene.image.Image

trait Power {

  val name : String;
  val range : Int;
  val cooldown : Long;
  def apply(user : Actor, targets : Vector[Entity], region : Region) : Unit;
  def canUse(user : Actor) : Boolean;
  def mkRegion(cX : Int = 0, cY : Int = 0) : Region;
  
  protected def addCooldown(user : Actor) = user.powerCooldowns.update(this, cooldown);
  
  val uiImage : Image;
  
}

object Power {
  
  val uiImage = new Image("file:res/sprites/ui/power-base.png");
  
}