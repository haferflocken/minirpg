package minirpg.model

class Region(coords : Array[(Int, Int)]) {

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
  
  def circle(centerX : Int, centerY : Int, radius : Int) : Region = {
    return ring(centerX, centerY, radius, radius);
  }
  
  def ring(centerX : Int, centerY : Int, radius : Int, thickness : Int) : Region = {
    // TODO
    
    return null;
  }
  
  def rectangle(centerX : Int, centerY : Int, width : Int, height : Int) : Region = {
    return rectangleRing(centerX, centerY, width, height, (width / 2) max (height / 2));
  }
  
  def rectangleRing(centerX : Int, centerY : Int, width : Int, height : Int, thickness : Int) : Region = {
    // TODO
    
    return null;
  }
  
  def semicircle(centerX : Int, centerY : Int, radius : Int, angleStart : Double, angleEnd : Double) : Region = {
    return arc(centerX, centerY, radius, radius, angleStart, angleEnd);
  }
  
  def arc(centerX : Int, centerY : Int, radius : Int, thickness : Int, angleStart : Double, angleEnd : Double) : Region = {
    // TODO
    
    return null;
  }
}