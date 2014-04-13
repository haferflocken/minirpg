package minirpg.model

abstract class Gear {
  
  val slots : Array[String];
  val powers : Array[Power];
  val skillBonuses : Map[String, Int];
  
}