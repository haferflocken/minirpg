package minirpg.entities

import minirpg.model.Entity
import scalafx.scene.Node
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import minirpg.model.Gear
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

class Corpse(val id : String, val name : String) extends Entity {
  
  val node : Node = new ImageView(Corpse.image);
  
  var gear : List[Gear] = Nil;
  
}

object Corpse {
  val image = new Image("file:res\\sprites\\tombstone.png");
}