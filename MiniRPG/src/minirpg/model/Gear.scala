package minirpg.model

import scala.collection.immutable.Map;
import scalafx.scene.image.Image

trait Gear {
  
  val name : String;
  val description : String;
  val equipSlots : Vector[String];
  val wieldSlots : Vector[String]; // The slots this gear occupies when wielded by an Actor. If null, this gear cannot be wielded.
  val powers : Vector[Power];
  val skillBonuses : Map[String, Int];
  
  lazy val equippedImages : Vector[Image] = null; // 0 is south, 1 is east, 2 is north, 3 is west
  
  def makeEntity(id : String) : Entity = new GearEntity(id, this);
  
  def placeEntity(world : World) : Entity = {
    val e = new GearEntity(world.makeEntityId, this);
    world.addEntity(e);
    return e;
  }
  
  override def toString = name;
  
}