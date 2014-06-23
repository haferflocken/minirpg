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
import minirpg.ui.SpriteView

class GearEntity(val id : String, val gear : Gear) extends Entity {
  
  val name = gear.name;
  override val description = gear.description;
  override val useable = true;
  val node : Node = SpriteView(GearEntity.sprite);
  val nodeWidth = GearEntity.sprite.frameWidth;
  val nodeHeight = GearEntity.sprite.frameHeight;
  
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
  val sprite = new Sprite(
      sheet = new Image("file:res\\sprites\\entities\\drop.png"),
      duration = new Duration(Duration(500)),
      frameWidth = 32,
      frameHeight = 32,
      columns = 4,
      rows = 2);
}

object GearEntityBuilder extends EntityBuilder[GearEntity] {
  
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
    
    val coords = extractCoords(args);
    if (coords == null) 
      return null;
    return new GearEntity(id, gear) {
      x = coords._1;
      y = coords._2;
    };
  }
  
  val buildName = "Gear";
  val buildClass = classOf[GearEntity];
}