package minirpg.model

abstract class Power {

  val name : String;
  val range : Int;
  def apply(user : Actor, targets : List[Actor], region : Region) : Unit;
  def canUse(user : Actor) : Boolean;
  
}