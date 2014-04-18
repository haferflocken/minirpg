package minirpg.model

trait Power {

  val name : String;
  val range : Int;
  def apply(user : Actor, targets : Vector[Entity], region : Region) : Unit;
  def canUse(user : Actor) : Boolean;
  def mkRegion(cX : Int = 0, cY : Int = 0) : Region;
  
}