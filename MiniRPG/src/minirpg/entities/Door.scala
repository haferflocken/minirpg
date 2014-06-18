package minirpg.entities

import minirpg.model.world._
import scalafx.scene.Node
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

class Door(val id : String, val name : String) extends Entity {
  
  override val useable = true;
  val node : Node = new ImageView(Corpse.image);
  val nodeWidth = Corpse.imageWidth;
  val nodeHeight = Corpse.imageHeight;
  
  var open = false;
  var locked = false;
  
  override def beUsedBy(user : Entity) = {
    if (locked) {
      println("The door is locked.");
    }
    else if (open) {
      // TODO
      open = false;
    }
    else {
      // TODO
      open = true;
    }
  };

}

object Door {
  
  val topOpen = new Image("");
  val topClosed = new Image("");
  val leftOpen = new Image("");
  val leftClosed = new Image("");
  val bottomOpen = new Image("");
  val bottomClosed = new Image("");
  val rightOpen = new Image("");
  val rightClosed = new Image("");
  
}