package minirpg.entities

import minirpg.model.Entity
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.image.ImageView

class ProximityMine(val id : String) extends Entity {
  
  val name = "Proximity Mine";
  val node = new ImageView(ProximityMine.image);

}

object ProximityMine {
  val image = new Image("file:res/sprites/entities/proximityMine.png");
}