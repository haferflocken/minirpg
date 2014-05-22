package minirpg.model.overworld

import scala.collection.mutable.ArrayBuffer
import scalafx.scene.image.Image

class Landmark(val name : String, val x : Int, val y : Int, val worldPath : String) {
  
  val coords = (x, y);
  
  def isAt(point : (Int, Int)) = x == point._1 && y == point._2;
  
  override def toString = s"$name ($x, $y) -> $worldPath";
  
  override def equals(other : Any) : Boolean = {
    if (!other.isInstanceOf[Landmark])
      return false;
    val l = other.asInstanceOf[Landmark];
    return hashCode == l.hashCode;
  }
  
  override def hashCode : Int = coords.hashCode;

}

object Landmark {
  
  val Image = new Image("file:res/sprites/landmark.png");
  
  val DestroyedImage = new Image("file:res/sprites/destroyedLandmark.png");
  
}