package minirpg.model

import scala.collection.mutable.LinkedHashMap

object Skills {
  
  val archery = "Archery";
  val blocking = "Blocking";
  val melee = "Melee";
  val pointing = "Pointing";
  val speed = "Speed";
  val style = "Style";
  
  val impactRes = "Impact Resistance";

  val all = Vector(archery, blocking, melee, pointing, speed, style, impactRes);
  val zeroTuples = all.map((_, 0));
  
  def makeZeroMap = new LinkedHashMap[String, Int] ++= zeroTuples;
}