package minirpg.entities.actors

import minirpg.model._
import minirpg.powers._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scala.collection.mutable.LinkedHashMap
import minirpg.model.world._
import scalafx.scene.image.Image
import scalafx.scene.image.ImageView
import minirpg.actorai.MoveRightAI

class Human(
    override val id : String,
    override val name : String,
    override val brain : ActorAI) extends Actor(
      id,
      name,
      Map(("Head", 1), ("Torso", 1), ("Legs", 1), ("Hands", 1), ("Feet", 1), ("Hip", 2), ("Back", 2)),
      Vector("Main Hand", "Off Hand"),
      Vector(Move, Use, Examine),
      Skills.zeroMap ++ Map(Skills.speed -> 200),
      brain) {
  
  val vitals = new LinkedHashMap[String, (Int, Int)] ++= Map("Blood" -> (100, 100), "Oxygen" -> (100, 100), "Energy" -> (100, 100));
  
  val node = new ImageView(Human.image);
  val nodeWidth = Human.imageWidth;
  val nodeHeight = Human.imageHeight;
}

object Human {
  val image = new Image("file:res\\sprites\\entities\\human-naked.png");
  val imageWidth = image.width().toInt;
  val imageHeight = image.height().toInt;
}

object HumanBuilder extends ActorBuilder[Human] {
  
  def build(id : String, args : Map[String, Any]) : Human = {
    val name = extractName(args);
    val pCoords = extractCoords(args);
    val gear = extractGear(args);
    if (name == null || pCoords == null || gear == null)
      return null;
    val brain = extractBrain(args);
    return new Human(id, name, brain) {
      x = pCoords._1;
      y = pCoords._2;
      for (g <- gear) equip(g);
    };
  }
  
  val buildName = "Human";
  val buildClass = classOf[Human];
}