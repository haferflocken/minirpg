package minirpg.entities

import minirpg.model.world._
import scalafx.scene.Node
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

class Door(val id : String, val name : String, val orientation : Door.Orientation) extends Entity {
  
  override val useable = true;
  private val _node = new ImageView(orientation.closedImage);
  val node : Node = _node;
  val nodeWidth = Corpse.imageWidth;
  val nodeHeight = Corpse.imageHeight;
  
  var locked = false;
  
  var _open = false;
  
  def open = _open;
  
  def open_=(o : Boolean) : Unit = {
    _open = o;
    if (_open == false)
      _node.image = orientation.closedImage;
    else
      _node.image = orientation.openImage;
  };
  
  override def beUsedBy(user : Entity) = {
    if (locked) {
      println("The door is locked.");
    }
    else
      open = !_open;
  };

}

object Door {
  
  sealed abstract class Orientation(val dir : Int, val openImage : Image, val closedImage : Image);
  case object Top extends Orientation(0, topOpen, topClosed);
  case object Left extends Orientation(1, leftOpen, leftClosed);
  case object Bottom extends Orientation(2, bottomOpen, bottomClosed);
  case object Right extends Orientation(3, rightOpen, rightClosed);
  
  val topOpen = new Image("file:res\\sprites\\entities\\doors\\topOpen.png");
  val topClosed = new Image("file:res\\sprites\\entities\\doors\\topClosed.png");
  val leftOpen = new Image("file:res\\sprites\\entities\\doors\\leftOpen.png");
  val leftClosed = new Image("file:res\\sprites\\entities\\doors\\leftClosed.png");
  val bottomOpen = new Image("file:res\\sprites\\entities\\doors\\bottomOpen.png");
  val bottomClosed = new Image("file:res\\sprites\\entities\\doors\\bottomClosed.png");
  val rightOpen = new Image("file:res\\sprites\\entities\\doors\\rightOpen.png");
  val rightClosed = new Image("file:res\\sprites\\entities\\doors\\rightClosed.png");
  
}