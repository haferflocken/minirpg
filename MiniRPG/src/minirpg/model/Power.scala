package minirpg.model

abstract class Power {

  def name : String;
  def range : Int;
  def apply(user : Actor, targets : List[Actor], region : Region) : Unit;
  
}