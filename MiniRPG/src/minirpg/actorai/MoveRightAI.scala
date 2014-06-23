package minirpg.actorai

import minirpg.model.world._
import minirpg.powers.Move
import minirpg.model.Region

object MoveRightAI extends ActorAI {
  
  val name = "move-right";
  
  def tick(actor : Actor, delta : Long) : Unit = {
    if (!actor.hasPath)
      Move(actor, null, Region.tile(actor.x + 1, actor.y));
  };

}