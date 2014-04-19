package minirpg.powers

import minirpg.model._
import scalafx.scene.paint.Color
import minirpg.entities.ProximityMine

object ExplosiveConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    val world = user.world;
    for (c <- region.coords) {
      val rX = world.tileGrid.tileWidth * (region.centerX + c._1) + world.tileGrid.tileWidth / 2;
      val rY = world.tileGrid.tileHeight * (region.centerY + c._2) + world.tileGrid.tileHeight / 2;
      val speeds = world.particleCanvas.randVelocities(20, 50, num = 10);
      world.particleCanvas.mkCircles(rX, rY, 2, Color.ORANGERED, speeds);
    }
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
    val speeds = world.particleCanvas.randVelocities(20, 50, num = 30);
    world.particleCanvas.mkCircles(rX, rY, 2, Color.AQUA, speeds);
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}

object TrapConduitAttack extends Power {
  
  val name = "Attack";
  val range = 1;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    val world = user.world;
    world.addEntity(new ProximityMine(world.makeEntityId) {
      x = region.centerX;
      y = region.centerY;
    });
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}