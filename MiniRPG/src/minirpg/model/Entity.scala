package minirpg.model

import scalafx.scene.Node

trait Entity {
  
  val id : String;
  
  var x : Int = 0;
  var y : Int = 0;
  var world : World = null;
  
  val node : Node;
  
  def tick : Unit;
  
  def beUsed(user : Entity) : Unit;

  override def toString() : String = s"$id ($x, $y)";
}