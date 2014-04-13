package minirpg.model

trait Gear {
  
  val name : String;
  val slots : Array[String];
  val powers : Array[Power];
  val skillBonuses : Map[String, Int];
  
  def makeEntity : Entity;
  
}