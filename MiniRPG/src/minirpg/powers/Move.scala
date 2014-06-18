package minirpg.powers

import minirpg.model._
import minirpg.model.world._
import scalafx.scene.image.Image

object Move extends Power {
  
  val name = "Move";
  val range = 4;
  val cooldown : Long = 0;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = 
    user.setMoveTarget(region.anchorX, region.anchorY);
  
  def canBeUsedBy(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
  val uiImage = new Image("file:res/sprites/ui/powers/move.png");

}