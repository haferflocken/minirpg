package minirpg.model

import scala.collection.mutable
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

class Region(val coords : Vector[(Int, Int)]) extends Canvasable {
  
  val width = coords.maxBy(_._1)._1 - coords.minBy(_._1)._1;
  val height = coords.maxBy(_._2)._2 - coords.minBy(_._2)._2;

  var centerX = 0;
  var centerY = 0;
  
  def ++(other : Region) : Region = {
    val outCoords = (coords ++ other.coords).toSet.toVector;
    return new Region(outCoords) {
      centerX = this.centerX;
      centerY = this.centerY;
    }
  }
  
  def contains(x : Int, y : Int) : Boolean = {
    val tX = x - centerX;
    val tY = y - centerY;
    
    for ((x, y) <- coords)
      if (tX == x && tY == y) return true;
    
    return false;
  }
  
  def clip(clipX : Int, clipY : Int, clipWidth : Int, clipHeight : Int) : Region = {
    val modClipX = clipX - centerX;
    val modClipY = clipY - centerY;
    val modClipX2 = modClipX + clipWidth;
    val modClipY2 = modClipY + clipHeight;
    
    filter(coord => coord._1 >= modClipX && coord._2 >= modClipY && coord._1 < modClipX2 && coord._2 < modClipY2);
  }
  
  def diff(other : Region) : Region = {
    val nCoords = normalizeCoords;
    val oNCoords = other.normalizeCoords;
    val diffCoords = nCoords diff oNCoords;
    val outCoords = diffCoords.map(c => ((c._1 - centerX, c._2 - centerY)));
    return new Region(outCoords) {
      centerX = this.centerX;
      centerY = this.centerY;
    }
  }
  
  def filter(f : ((Int, Int)) => Boolean) : Region = {
    val outCoords = coords.filter(f);
    return new Region(outCoords) {
      centerX = this.centerX;
      centerY = this.centerY;
    }
  }
  
  def intersect(other : Region) : Region = {
    val nCoords = normalizeCoords;
    val oNCoords = other.normalizeCoords;
    val diffCoords = nCoords intersect oNCoords;
    val outCoords = diffCoords.map(c => ((c._1 - centerX, c._2 - centerY)));
    return new Region(outCoords) {
      centerX = this.centerX;
      centerY = this.centerY;
    }
  }
  
  def mkCanvas(imageWidth : Int, imageHeight : Int) : Canvas = {
    val canvas = new Canvas(imageWidth, imageHeight);
    val g = canvas.graphicsContext2D;
    val tileWidth = imageWidth / width;
    val tileHeight = imageHeight / height;
    
    g.fill = Color.rgb(255, 0, 0, 0.5f);
    for (c <- coords) {
      val x = c._1 + width / 2;
      val y = c._2 + height / 2;
      g.fillRect(x * imageWidth / width, y * imageHeight / height, tileWidth, tileHeight);
    }
    
    return canvas;
  }
  
  def normalizeCoords : Vector[(Int, Int)] = coords.map(c => ((c._1 + centerX, c._2 + centerY)));
  
}

object Region {
  
  def tile(x : Int, y : Int) : Region = new Region(Vector((0, 0))) { centerX = x; centerY = y; };
  
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
  
  def rectangle(centerX : Int, centerY : Int, width : Int, height : Int) : Region = {
    return rectangleRing(centerX, centerY, width, height, ((width / 2) max (height / 2)) + 1);
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
    return new Region(buff.toVector) {
      centerX = cX;
      centerY = cY;
    };
  }
  
  def semicircle(centerX : Int, centerY : Int, radius : Int, angleStart : Double, angleEnd : Double) : Region = {
    return arc(centerX, centerY, radius, radius, angleStart, angleEnd);
  }
  
  def arc(centerX : Int, centerY : Int, radius : Int, thickness : Int, angleStart : Double, angleEnd : Double) : Region = {
    // TODO
    
    return null;
  }
}