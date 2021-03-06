package minirpg.model.world

import scala.collection.immutable.Map
import scalafx.scene.image.Image
import minirpg.model.world._
import minirpg.entities._

trait Gear {
  
  val name : String;
  val description : String;
  val equipSlots : Vector[String];
  val wieldSlots : Vector[String]; // The slots this gear occupies when wielded by an Actor.
                                   // If null, this gear cannot be wielded.
  val powers : Vector[Power]; // The powers this gear grants an actor.
                              // If wieldSlots != null, the powers are only granted when the actor is wielding this gear.
                              // Additionally, if wieldSlots != null, powers cannot be null.
  val skillBonuses : Map[String, Int];
  
  val uiWieldImage : Image;
  val uiUnwieldImage : Image;
  
  def makeEntity(id : String) : Entity = new GearEntity(id, this);
  
  def placeEntity(world : World) : Entity = {
    val e = new GearEntity(world.makeEntityId, this);
    world.addEntity(e);
    return e;
  }
  
  override def equals(other : Any) : Boolean = {
    if (!other.isInstanceOf[Gear])
      return false;
    return hashCode == other.hashCode;
  }
  
  override def hashCode : Int = {
    var hash = name.hashCode;
    if (description != null) hash += description.hashCode * 3;
    hash += equipSlots.hashCode * 5;
    if (wieldSlots != null) hash += wieldSlots.hashCode * 7;
    if (powers != null) hash += powers.hashCode * 11;
    hash += skillBonuses.hashCode * 13;
    return hash;
  }
  
  override def toString = name;
  
}

object Gear {
  
  val uiWieldImage = new Image("file:res/sprites/ui/wielded-gear-base.png");
  val uiUnwieldImage = new Image("file:res/sprites/ui/unwielded-gear-base.png");
  val uiImage = new Image("file:res/sprites/ui/gear-base.png");
  
}