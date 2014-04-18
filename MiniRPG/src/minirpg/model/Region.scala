package minirpg.model

import scala.collection.mutable.ArrayBuffer

class Region(val coords : Vector[(Int, Int)]) {
  
  val width = coords.maxBy(_._1)._1 - coords.minBy(_._1)._1;
  val height = coords.maxBy(_._2)._2 - coords.minBy(_._2)._2;

  var centerX = 0;
  var centerY = 0;
  
  def contains(x : Int, y : Int) : Boolean = {
    var tX = x - centerX;
    var tY = y - centerY;
    
    coords.foreach((c : (Int, Int)) => {
      if (tX == c._1 && tY == c._2)
        return true;
    });
    return false;
  }
}

object Region {
  
  def tile(x : Int, y : Int) : Region = new Region(Vector((0, 0))) { centerX = x; centerY = y; };
  
  def circle(centerX : Int, centerY : Int, radius : Int) : Region = {
    return ring(centerX, centerY, radius, radius);
  }
  
  def ring(centerX : Int, centerY : Int, radius : Int, thickness : Int) : Region = {
    // TODO
    
    return null;
  }
  
  def rectangle(centerX : Int, centerY : Int, width : Int, height : Int) : Region = {
    return rectangleRing(centerX, centerY, width, height, ((width / 2) max (height / 2)) + 1);
  }
  
  def rectangleRing(cX : Int, cY : Int, width : Int, height : Int, thickness : Int = 1) : Region = {
    val buff = new ArrayBuffer[(Int, Int)];
    for (t <- 0 until thickness) {
      val left = cX - width / 2 + t;
      val top = cY - height / 2 + t;
      val right = cX + width / 2 - t;
      val bottom = cY + height / 2 - t;
      
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