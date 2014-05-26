package minirpg.model

import scala.collection.mutable.LinkedHashMap

object Skills {
  
  // Skill skills.
  val archery = "Archery";
  val blocking = "Blocking";
  val melee = "Melee";
  val pointing = "Pointing";
  val speed = "Speed";						// A measure of the actor's speed, in (% of a tile)/second
  val style = "Style";
  
  // Physical resistances.
  val pierceRes = "Pierce Resistance";
  val slashRes = "Slash Resistance";
  val bashRes = "Bash Resistance";
  
  // Mental resistances.
  val emotionalRes = "Emotional Resistance";
  val persaudeRes = "Persaude Resistance";
  
  // Magical resistances.
  val cyanRes = "Cyan Resistance";
  val magentaRes = "Magenta Resistance";
  val yellowRes = "Yellow Resistance";
  val blackRes = "Black Resistance";
  
  // Other
  val confidence = "Confidence";
  val threat = "Threat";

  // Utilities.
  val all = Vector(
    archery, blocking, melee, pointing, speed, style,
    pierceRes, slashRes, bashRes,
    emotionalRes, persaudeRes,
    cyanRes, magentaRes, yellowRes, blackRes);
  val zeroTuples = all.map((_, 0));
  val zeroMap = zeroTuples.toMap;
  
  def makeZeroMap = new LinkedHashMap[String, Int] ++= zeroTuples;
}