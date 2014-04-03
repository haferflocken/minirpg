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