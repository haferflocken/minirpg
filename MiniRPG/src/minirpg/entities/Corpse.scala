package minirpg.entities

import minirpg.model._
import scalafx.scene.Node
import minirpg.model.Gear
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image
import minirpg.entities.GearEntity

class Corpse(val id : String, val name : String) extends Entity {
  
  override val useable = true;
  val node : Node = new ImageView(Corpse.image);
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
}