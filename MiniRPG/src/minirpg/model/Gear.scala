package minirpg.model

import scala.collection.immutable.Map;

trait Gear {
  
  val name : String;
  val description : String;
  val slots : Vector[String];
  val powers : Vector[Power];
  val skillBonuses : Map[String, Int];
  
  def makeEntity(id : String) : Entity = new GearEntity(id, this);
  
  def placeEntity(world : World) : Entity = {
    val e = new GearEntity(world.makeEntityId, this);
    world.addEntity(e);
    return e;
  }
  
}