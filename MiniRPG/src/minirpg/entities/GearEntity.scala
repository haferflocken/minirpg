package minirpg.entities

import scalafx.scene.Node
import scalafx.scene.image.{ ImageView, Image }
import minirpg.gearMap
import minirpg.model._
import minirpg.model.world._
import minirpg.model.world.Gear
import minirpg.ui.animation.SpriteTransition
import scalafx.util.Duration
import scalafx.geometry.Rectangle2D

class GearEntity(val id : String, val gear : Gear) extends Entity {
  
  val name = gear.name;
  override val description = gear.description;
  override val useable = true;
  val node : Node = GearEntity.mkImageView;
  val nodeWidth = GearEntity.frameWidth;
  val nodeHeight = GearEntity.frameHeight;
  
  override def beUsedBy(user : Entity) : Unit = {
    if (user.isInstanceOf[Actor]) {
      val actor = user.asInstanceOf[Actor];
      // TODO give actor the option to equip the gear or just examine it.
      actor.equip(gear);
      world.removeEntity(this);
    }
  }
  
}

object GearEntity {
  val sheet = new Image("file:res\\sprites\\entities\\drop.png");
  val duration = new Duration(Duration(500));
  val frameWidth = 32;
  val frameHeight = 32;
  val columns = 3;
  val rows = 2;
  
  def mkImageView : ImageView = {
    val out = new ImageView(sheet);
    out.viewport = new Rectangle2D(0, 0, frameWidth, frameHeight);
    val anim = new SpriteTransition(out, duration, frameWidth, frameHeight, columns, rows);
    anim.play();
    return out;
  };
}

object GearEntityBuilder extends Builder[GearEntity] {
  
  def build(id : String, args : Map[String, Any]) : GearEntity = {
    val rawType = args.getOrElse("type", null);
    if (rawType == null || !rawType.isInstanceOf[String]) {
      println("Argument \"type\" of class \"Gear\" must be a string.");
      return null;
    }
    val typeString = rawType.asInstanceOf[String];
    val gear = gearMap.getOrElse(typeString, null);
    if (gear == null) {
      println("Failed to find gear of type \"" + typeString + "\".");
      return null;
    }
    
    val rawX = args.getOrElse("x", null);
    if (rawX == null || !rawX.isInstanceOf[Double]) {
      println("Argument \"x\" of class \"Gear\" must be a number.");
      return null;
    }
    val pX = rawX.asInstanceOf[Double].intValue;
    val rawY = args.getOrElse("y", null);
    if (rawY == null || !rawY.isInstanceOf[Double]) {
      println("Argument \"y\" of class \"Gear\" must be a number.");
      return null;
    }
    val pY = rawY.asInstanceOf[Double].intValue;
    return new GearEntity(id, gear) {
      x = pX;
      y = pY;
    };
  }
  
  val buildName = "Gear";
  val buildClass = classOf[GearEntity];
}