package minirpg.entities

import minirpg.model.world._
import scalafx.scene.Node
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

class Door(val id : String, val name : String, val orientation : Door.Orientation) extends Entity {
  
  override val useable = true;
  private val _node = new ImageView(orientation.openImage);
  val node : Node = _node;
  val nodeWidth = Corpse.imageWidth;
  val nodeHeight = Corpse.imageHeight;
  
  var locked = false;
  
  var _open = true;
  
  def open = _open;
  
  def open_=(o : Boolean) : Unit = {
    if (o == _open)
      return;
    _open = o;
    if (_open == false) {
      _node.image = orientation.closedImage;
      world.disableNavConnection(x, y, x + orientation.dX, y + orientation.dY);
    }
    else {
      _node.image = orientation.openImage;
      world.enableNavConnection(x, y, x + orientation.dX, y + orientation.dY);
    }
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
  
  sealed abstract class Orientation(val dX : Int, val dY : Int, val openImage : Image, val closedImage : Image);
  case object Top extends Orientation(0, -1, topOpen, topClosed);
  case object Left extends Orientation(-1, 0, leftOpen, leftClosed);
  case object Bottom extends Orientation(0, 1, bottomOpen, bottomClosed);
  case object Right extends Orientation(1, 0, rightOpen, rightClosed);
  
  val topOpen = new Image("file:res\\sprites\\entities\\doors\\topOpen.png");
  val topClosed = new Image("file:res\\sprites\\entities\\doors\\topClosed.png");
  val leftOpen = new Image("file:res\\sprites\\entities\\doors\\leftOpen.png");
  val leftClosed = new Image("file:res\\sprites\\entities\\doors\\leftClosed.png");
  val bottomOpen = new Image("file:res\\sprites\\entities\\doors\\bottomOpen.png");
  val bottomClosed = new Image("file:res\\sprites\\entities\\doors\\bottomClosed.png");
  val rightOpen = new Image("file:res\\sprites\\entities\\doors\\rightOpen.png");
  val rightClosed = new Image("file:res\\sprites\\entities\\doors\\rightClosed.png");
  
}

object DoorBuilder extends EntityBuilder[Door] {
  
  def build(id : String, args : Map[String, Any]) : Door = {
    val rawOrientation = extract[String]("orientation", args, null);
    val orientation : Door.Orientation = rawOrientation match {
      case "top" => Door.Top;
      case "left" => Door.Left;
      case "bottom" => Door.Bottom;
      case "right" => Door.Right;
      case _ => null;
    }
    if (orientation == null)
      return null;
    
    val name = extractName(args);
    if (name == null)
      return null;
    
    val coords = extractCoords(args);
    if (coords == null)
      return null;
    return new Door(id, name, orientation) {
      x = coords._1;
      y = coords._2;
    };
  }
  
  val buildName = "Door";
  val buildClass = classOf[Door];
}