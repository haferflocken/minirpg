package minirpg.actorai

import minirpg.model.world._
import minirpg.powers.Move
import minirpg.model.Region

object MoveRightAI extends ActorAI {
  
  def tick(actor : Actor, delta : Long) : Unit = {
    if (actor.path == null)
      Move(actor, null, Region.tile(actor.x + 1, actor.y));
  };

}