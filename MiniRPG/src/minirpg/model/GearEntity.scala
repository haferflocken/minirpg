package minirpg.model

import scalafx.scene.Node
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scala.collection.mutable.HashMap
import minirpg.gear._

class GearEntity(val id : String, val gear : Gear) extends Entity {
  
  val node : Node = new Rectangle() {
    fill = Color.YELLOW;
    width = 32;
    height = 32;
  };
  
  def beUsed(user : Entity) : Unit = {
    if (user.isInstanceOf[Actor]) {
      val actor = user.asInstanceOf[Actor];
      // TODO give actor the option to equip the gear.
    }
  }
  
}

object GearEntityBuilder extends Builder[GearEntity] {
  
  private val gearMap =
    Vector[Gear](
        Shortsword
    ).map((e) => (e.name, e)).toMap[String, Gear];
  
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