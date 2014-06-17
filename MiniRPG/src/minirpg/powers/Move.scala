package minirpg.powers

import minirpg.model._
import minirpg.model.world._
import scalafx.scene.image.Image

object Move extends Power {
  
  val name = "Move";
  val range = 64;
  val cooldown : Long = 0;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = 
    user.setMoveTarget(region.anchorX, region.anchorY);
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
  val uiImage = new Image("file:res/sprites/ui/power-base.png");

}