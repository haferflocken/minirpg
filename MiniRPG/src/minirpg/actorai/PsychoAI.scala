package minirpg.actorai

import minirpg.model.world._
import minirpg.powers.Move
import minirpg.model.Region
import scala.collection.mutable

object PsychoAI extends ActorAI {
  
  val name = "psycho";
  
  private val targetOf = new mutable.HashMap[Actor, Actor];
  private val nearRegion = Region.rectangle(0, 0, 5, 5);
  
  def tick(actor : Actor, delta : Long) : Unit = {
    // Find something to be mad at.
    var target = targetOf.getOrElse(actor, null);
    if (target == null) {
      nearRegion.anchorX = actor.x;
      nearRegion.anchorY = actor.y;
      val nearby = actor.world.getEntitiesIn(nearRegion).collect( {case a : Actor => a} );
      if (nearby.nonEmpty) {
        target = nearby(0);
        targetOf(actor) = target;
      }
    }
    
    // Murder it.
    if (target != null && actor.path == null && actor.powers.contains(Move) && Move.canBeUsedBy(actor)) {
      Move(actor, null, Region.tile(target.x, target.y));
    }
  };

}