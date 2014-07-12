package minirpg.model

import scala.collection.mutable
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scala.collection.immutable.HashSet

class Region(val coords : HashSet[(Int, Int)]) extends Canvasable {
  
  val leftmost = coords.minBy(_._1)._1;
  val rightmost = coords.maxBy(_._1)._1;
  val topmost = coords.minBy(_._2)._2;
  val bottommost = coords.maxBy(_._2)._2;
  
  val width = rightmost - leftmost + 1;
  val height = bottommost - topmost + 1;

  var anchorX = 0;
  var anchorY = 0;
  
  def leftBound = anchorX + leftmost;
  def rightBound = anchorX + rightmost;
  def topBound = anchorY + topmost;
  def bottomBound = anchorY + bottommost;
  
  def bounds = (leftBound, topBound, rightBound, bottomBound);
  
  def ++(other : Region) : Region = {
    val outCoords = coords ++ other.coords;
    val (aX, aY) = (anchorX, anchorY);
    return new Region(outCoords) {
      anchorX = aX;
      anchorY = aY;
    }
  }
  
  def contains(x : Int, y : Int) : Boolean = {
    val tX = x - anchorX;
    val tY = y - anchorY;
    return coords.contains((tX, tY));
  }
  
  def containsAny(ps : Iterable[(Int, Int)]) : Boolean = {
    for (p <- ps) {
      if (contains(p._1, p._2))
        return true;
    }
    return false;
  }
  
  def containsAny[A](ps : Iterable[A])(implicit toCoord : (A => (Int, Int))) : Boolean = {
    for (p <- ps) {
      val c = toCoord(p);
      if (contains(c._1, c._2))
        return true;
    }
    return false;
  }
  
  def clip(clipX : Int, clipY : Int, clipWidth : Int, clipHeight : Int) : Region = {
    val modClipX = clipX - anchorX;
    val modClipY = clipY - anchorY;
    val modClipX2 = modClipX + clipWidth;
    val modClipY2 = modClipY + clipHeight;
    return filter(coord => coord._1 >= modClipX && coord._2 >= modClipY && coord._1 < modClipX2 && coord._2 < modClipY2);
  }
  
  def diff(other : Region) : Region = {
    val nCoords = normalizeCoords;
    val oNCoords = other.normalizeCoords;
    val diffCoords = nCoords diff oNCoords;
    val outCoords = diffCoords.map(c => ((c._1 - anchorX, c._2 - anchorY)));
    val (aX, aY) = (anchorX, anchorY);
    return new Region(outCoords) {
      anchorX = aX;
      anchorY = aY;
    }
  }
  
  def filter(f : ((Int, Int)) => Boolean) : Region = {
    val outCoords = coords.filter(f);
    val (aX, aY) = (anchorX, anchorY);
    return new Region(outCoords) {
      anchorX = aX;
      anchorY = aY;
    }
  }
  
  def intersect(other : Region) : Region = {
    val nCoords = normalizeCoords;
    val oNCoords = other.normalizeCoords;
    val diffCoords = nCoords intersect oNCoords;
    val outCoords = diffCoords.map(c => ((c._1 - anchorX, c._2 - anchorY)));
    val (aX, aY) = (anchorX, anchorY);
    return new Region(outCoords) {
      anchorX = aX;
      anchorY = aY;
    }
  }
  
  def mkCanvas(imageWidth : Int, imageHeight : Int) : Canvas = {
    val canvas = new Canvas(imageWidth, imageHeight);
    val g = canvas.graphicsContext2D;
    val tileWidth = imageWidth / width;
    val tileHeight = imageHeight / height;
    
    g.fill = Color.rgb(255, 0, 0, 0.5f);
    for (c <- coords) {
      val x = c._1 - leftmost;
      val y = c._2 - topmost;
      g.fillRect(x * imageWidth / width, y * imageHeight / height, tileWidth, tileHeight);
    }
    
    return canvas;
  }
  
  def normalizeCoords : HashSet[(Int, Int)] = coords.map(c => ((c._1 + anchorX, c._2 + anchorY)));
  
  def zeroCoords : HashSet[(Int, Int)] = coords.map(c => ((c._1 - leftmost, c._2 - topmost)));
  
}

object Region {
  
  def tile(x : Int, y : Int) : Region = new Region(HashSet((0, 0))) { anchorX = x; anchorY = y; };
  
  def circle(centerX : Int, centerY : Int, radius : Int) : Region = {
    return ring(centerX, centerY, radius, radius);
  }
  
  def ring(cX : Int, cY : Int, radius : Int, thickness : Int) : Region = {
    val innerRadius = radius - thickness;
    val rect = rectangle(cX, cY, radius * 2, radius * 2);
    
    return rect.filter(c => {
      val h = Math.sqrt(c._1 * c._1 + c._2 * c._2);
      h >= innerRadius && h <= radius;
    });
  }
  
  def rectangle(cX : Int, cY : Int, width : Int, height : Int) : Region = {
    val left = -width / 2;
    val top = -height / 2;
    val right = -left;
    val bottom = -top;
     
    val coords = for (i <- left to right; j <- top to bottom) yield (i, j);
    return new Region(HashSet() ++ coords) {
      anchorX = cX;
      anchorY = cY;
    }
  }
  
  def rectangleRing(cX : Int, cY : Int, width : Int, height : Int, thickness : Int = 1) : Region = {
    val buff = new mutable.HashSet[(Int, Int)];
    for (t <- 0 until thickness) {
      val left = -width / 2 + t;
      val top = -height / 2 + t;
      val right = width / 2 - t;
      val bottom = height / 2 - t;
      
      for (i <- left to right) buff += ((i, top), (i, bottom));
      for (j <- top + 1 to bottom - 1) buff += ((left, j), (right, j));
    }
    return new Region(HashSet() ++ buff) {
      anchorX = cX;
      anchorY = cY;
    };
  }
  
  def semicircle(centerX : Int, centerY : Int, radius : Int, angleStart : Double, angleEnd : Double) : Region = {
    return arc(centerX, centerY, radius, radius, angleStart, angleEnd);
  }
  
  def arc(centerX : Int, centerY : Int, radius : Int, thickness : Int, angleStart : Double, angleEnd : Double) : Region = {
    val r = ring(centerX, centerY, radius, thickness);
    
    return r.filter(c => {
      val a = Math.atan2(c._2, c._1);
      a >= angleStart && a < angleEnd;
    });
  }
}