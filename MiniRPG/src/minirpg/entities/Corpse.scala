package minirpg.entities

import minirpg.model._
import scalafx.scene.Node
import minirpg.model.world.Gear
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image
import minirpg.model.world.Entity
import minirpg.model.world.Actor

class Corpse(val id : String, val name : String) extends Entity {
  
  override val useable = true;
  val node : Node = new ImageView(Corpse.image);
  val nodeWidth = Corpse.imageWidth;
  val nodeHeight = Corpse.imageHeight;
  
  var gear : List[Gear] = Nil;
  
  override def beUsedBy(user : Entity) = {
    if (gear != Nil && user.isInstanceOf[Actor]) {
      // Explode into a bunch of goodies.
      for (g <- gear) {
        val spot = world.getSpotNextTo(x, y);
        world.addEntity(new GearEntity(world.makeEntityId, g) {
          x = spot._1;
          y = spot._2;
        })
      }
      gear = Nil;
    }
  }
  
}

object Corpse {
  val image = new Image("file:res\\sprites\\entities\\tombstone.png");
  val imageWidth = image.width().toInt;
  val imageHeight = image.height().toInt;
}