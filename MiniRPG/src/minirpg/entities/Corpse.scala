package minirpg.entities

import minirpg.model.Entity
import scalafx.scene.Node
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import minirpg.model.Gear

class Corpse(val id : String) extends Entity {
  
  val node : Node = new Circle() {
    fill = Color.GRAY;
    radius = 4;
  };
  
  var gear : List[Gear] = Nil;
  
}