package minirpg.entities.actors

import minirpg.model._
import minirpg.powers._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scala.collection.mutable.LinkedHashMap

class Human(id : String, name : String) extends Actor(
    id,
    name,
    Map(("Head", 1), ("Torso", 1), ("Legs", 1), ("Hands", 1), ("Feet", 1), ("Hip", 2), ("Back", 2)),
    Vector("Main Hand, Off Hand"),
    Vector(Move),
    Skills.zeroMap ++ Map(Skills.speed -> 200)) {
  
  val vitals = new LinkedHashMap[String, Int] ++= Map("Blood" -> 100, "Oxygen" -> 100, "Energy" -> 100);
  
  val node = new Rectangle() {
    fill = Color.RED;
    width = 32;
    height = 32;
  };
}

object HumanBuilder extends ActorBuilder[Human] {
  
  def build(id : String, args : Map[String, Any]) : Human = {
    val name = extractName(args);
    val pCoords = extractCoords(args);
    val gear = extractGear(args);
    if (name == null || pCoords == null || gear == null)
      return null;
    return new Human(id, name) {
      x = pCoords._1;
      y = pCoords._2;
      for (g <- gear) equip(g);
    };
  }
  
  val buildName = "Human";
  val buildClass = classOf[Human];
}