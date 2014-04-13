package minirpg.model

import scala.collection.mutable.LinkedHashMap

object Skills {
  
  val agility = "Agility";
  val archery = "Archery";
  val blocking = "Blocking";
  val melee = "Melee";
  val pointing = "Pointing";
  val style = "Style";

  val all = Vector(agility, archery, blocking, melee, pointing, style);
  val zeroTuples = all.map((_, 0));
  
  def makeZeroMap = new LinkedHashMap[String, Int] ++= zeroTuples;
}