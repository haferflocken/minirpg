package minirpg.powers

import minirpg.TENTOTHE9
import minirpg.model._
import minirpg.gear._
import scalafx.scene.paint.Color
import minirpg.entities.ProximityMine

trait ConduitAttack extends Power {
  
  protected def mkParticles(world : World, region : Region) : Unit;
  
  val orb : Gear;
  val particleColor : Color;
  
  def canUse(user : Actor) = user.equipped.contains(orb);
  
}

trait ExplosiveConduitAttack extends ConduitAttack {
  
  protected def mkParticles(world : World, region : Region) : Unit = {
    for (c <- region.coords) {
      val rX = world.tileGrid.tileWidth * (region.centerX + c._1) + world.tileGrid.tileWidth / 2;
      val rY = world.tileGrid.tileHeight * (region.centerY + c._2) + world.tileGrid.tileHeight / 2;
      val speeds = world.particleCanvas.randVelocities(20, 50, num = 10);
      world.particleCanvas.mkCircles(rX, rY, 2, particleColor, speeds);
    }
  }
  
  val range = 6;
  val cooldown : Long = TENTOTHE9;
  
  def mkRegion(cX : Int, cY : Int) = Region.rectangle(cX, cY, 3, 3);
  
}

trait FocusedConduitAttack extends ConduitAttack {
  
  protected def mkParticles(world : World, region : Region) : Unit = {
    val rX = world.tileGrid.tileWidth * region.centerX + world.tileGrid.tileWidth / 2;
    val rY = world.tileGrid.tileHeight * region.centerY + world.tileGrid.tileHeight / 2;
    val speeds = world.particleCanvas.randVelocities(20, 50, num = 30);
    world.particleCanvas.mkCircles(rX, rY, 2, Color.AQUA, speeds);
  }

  val range = 7;
  val cooldown : Long = TENTOTHE9 / 2;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}

trait TrapConduitAttack extends ConduitAttack {
  
  protected def mkParticles(world : World, region : Region) : Unit = {}
  
  val range = 4;
  val cooldown : Long = TENTOTHE9 * 3 / 2;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
}

object RedExplosiveConduitAttack extends ExplosiveConduitAttack {
  
  val name = "Red Explosion";
  val orb = RedOrb;
  val particleColor = Color.RED;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    addCooldown(user);
    mkParticles(user.world, region);
  }
}

object RedFocusedConduitAttack extends FocusedConduitAttack {
  
  val name = "Red Attack";
  val orb = RedOrb;
  val particleColor = Color.RED;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    addCooldown(user);
    mkParticles(user.world, region);
  }
}

object RedTrapConduitAttack extends TrapConduitAttack {
  
  val name = "Red Trap"
  val orb = RedOrb;
  val particleColor = Color.RED;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    // TODO
    addCooldown(user);
    val world = user.world;
    world.addEntity(new ProximityMine(world.makeEntityId) {
      x = region.centerX;
      y = region.centerY;
    });
  }
}