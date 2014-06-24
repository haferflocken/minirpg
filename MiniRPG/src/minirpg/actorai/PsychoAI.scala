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
    if (target != null && !actor.hasPath && !actor.isNextTo(target.x, target.y) && actor.powers.contains(Move) && Move.canBeUsedBy(actor)) {
      nearRegion.anchorX = actor.x;
      nearRegion.anchorY = actor.y;
      if (nearRegion contains (target.x, target.y)) {
        val (x, y) = actor.world.getSpotNextTo(target.x, target.y);
        Move(actor, null, Region.tile(x, y));
      }
      else
        targetOf -= actor;
    }
  };

}