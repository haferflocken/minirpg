package minirpg.powers

import minirpg.model._

object ExplosiveConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.rectangle(cX, cY, 3, 3);
  
}

object FocusedConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    val world = user.world;
    val rX = world.tileGrid.tileWidth * region.centerX + world.tileGrid.tileWidth / 2;
    val rY = world.tileGrid.tileHeight * region.centerY + world.tileGrid.tileHeight / 2;
    for (i <- 1 to 10) {
      val (xSpeed, ySpeed) = world.particleCanvas.randVelocity(20, 50);
      world.particleCanvas.mkPoint(rX, rY, xSpeed, ySpeed);
    }
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}

object TrapConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}