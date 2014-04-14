package minirpg.model

import scalafx.scene.Node

trait Entity {
  
  val id : String;
  
  var x : Int = 0;
  var y : Int = 0;
  var world : World = null;
  
  val node : Node;
  
  def tick(delta : Long) : Unit = {};
  
  def beUsedBy(user : Entity) : Unit = {};

  override def toString() : String = s"$id ($x, $y)";
}

abstract class EntityBuilder[E <: Entity] extends Builder[Entity] {
  
  def extractCoords(args : Map[String, Any]) : (Int, Int) = {
    val pX = extract[Double]("x", args, Double.MinValue);
    val pY = extract[Double]("y", args, Double.MinValue);
    if (pX == Double.MinValue || pY == Double.MinValue)
      return null;
    return (pX.intValue, pY.intValue);
  }
  
}