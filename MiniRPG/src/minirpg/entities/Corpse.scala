package minirpg.entities

import minirpg.model.Entity
import scalafx.scene.Node
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import minirpg.model.Gear
import scalafx.scene.image.ImageView

class Corpse(val id : String) extends Entity {
  
  val node : Node = new ImageView("file:res\\sprites\\tombstone.png");
  
  var gear : List[Gear] = Nil;
  
}