package minirpg.model.overworld

import scala.collection.mutable.ArrayBuffer

class Landmark(val name : String, val x : Int, val y : Int) {
  
  def isAt(point : (Int, Int)) = x == point._1 && y == point._2;
  
  override def toString = name + s" ($x, $y)";
  
  override def equals(other : Any) : Boolean = {
    if (!other.isInstanceOf[Landmark])
      return false;
    val l = other.asInstanceOf[Landmark];
    return hashCode == l.hashCode;
  }
  
  override def hashCode : Int = x & (y << 4);

}

object Landmark {
  
  val RandomNames = Vector[String](
      "Fort Ozymandias", "Majestic Necropolis", "Port Swagger", "Log Town", "Tiara Train Station");
  
  def randomName = RandomNames(Math.random * RandomNames.length toInt);
  
  def nRandomNames(n : Int) : Vector[String] = {
    val namePool = new ArrayBuffer[String] ++= RandomNames;
    while (namePool.length < n) {
      namePool ++= RandomNames;
    }
    
    var out = Vector[String]();
    for (k <- 0 until n) {
      val i = (Math.random * namePool.length) toInt;
      out = namePool(i) +: out;
      namePool.remove(i);
    }
    return out;
  }
  
}