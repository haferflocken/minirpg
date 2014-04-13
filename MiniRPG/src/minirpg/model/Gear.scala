package minirpg.model

trait Gear {
  
  val slots : Array[String];
  val powers : Array[Power];
  val skillBonuses : Map[String, Int];
  
}