package minirpg.model

import scala.collection.mutable.LinkedHashMap

object Skills {
  
  val archery = "Archery";
  val blocking = "Blocking";
  val melee = "Melee";
  val pointing = "Pointing";
  val speed = "Speed";          // A measure of the actor's speed, in (% of a tile)/second
  val style = "Style";
  
  val impactRes = "Impact Resistance";

  val all = Vector(archery, blocking, melee, pointing, speed, style, impactRes);
  val zeroTuples = all.map((_, 0));
  val zeroMap = zeroTuples.toMap;
  
  def makeZeroMap = new LinkedHashMap[String, Int] ++= zeroTuples;
}