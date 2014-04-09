package minirpg.model

import scalafx.scene.Node

trait Entity {
  
  var x : Int = 0;
  var y : Int = 0;
  var world : World = null;
  
  val node : Node;

}