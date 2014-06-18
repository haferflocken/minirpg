package minirpg.model.world

import minirpg.powers.Move
import minirpg.model.Region
import minirpg.model.Builder

abstract class ActorAI {
  
  val name : String;
  
  // Issue any commands to the actor that it might need this tick.
  def tick(actor : Actor, delta : Long) : Unit;

}
