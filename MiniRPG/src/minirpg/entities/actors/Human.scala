package minirpg.entities.actors

import minirpg.model._
import minirpg.powers._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color

class Human(id : String, name : String) extends Actor(
    id, name, Array("Head", "Torso", "Legs", "Hands", "Feet", "Main Hand", "Off Hand"), Vector(Move)) {
  
  val node = new Rectangle() {
    fill = Color.RED;
    width = 32;
    height = 32;
  };
}

object HumanBuilder extends Builder[Human] {
  
  def build(id : String, args : Map[String, Any]) : Human = {
    val rawName = args.getOrElse("name", null);
    if (rawName == null || !rawName.isInstanceOf[String]) {
      println("Argument \"name\" of class \"Human\" must be a string.");
      return null;
    }
    val pName = rawName.asInstanceOf[String];
    val rawX = args.getOrElse("x", null);
    if (rawX == null || !rawX.isInstanceOf[Double]) {
      println("Argument \"x\" of class \"Human\" must be a number.");
      return null;
    }
    val pX = rawX.asInstanceOf[Double].intValue;
    val rawY = args.getOrElse("y", null);
    if (rawY == null || !rawY.isInstanceOf[Double]) {
      println("Argument \"y\" of class \"Human\" must be a number.");
      return null;
    }
    val pY = rawY.asInstanceOf[Double].intValue;
    return new Human(id, pName) {
      x = pX;
      y = pY;
    };
  }
  
  val buildName = "Human";
  val buildClass = classOf[Human];
}