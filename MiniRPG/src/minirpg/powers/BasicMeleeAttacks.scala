package minirpg.powers

import minirpg.TENTOTHE9
import minirpg.model._
import minirpg.model.world._
import scalafx.scene.image.Image

object ShortswordAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  val cooldown : Long = TENTOTHE9 / 2;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    addCooldown(user);
  }
  
  def canBeUsedBy(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
  val uiImage = Power.uiImage;
  
}