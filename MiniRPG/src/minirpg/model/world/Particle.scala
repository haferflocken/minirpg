package minirpg.model.world

import scalafx.scene.Node
import scalafx.scene.Group

trait AbstractParticle {
  
  val node : Node;
  val group : Group;
  
  def die : Unit = group.children.remove(node);

}

class Particle(override val node : Node, override val group : Group) extends AbstractParticle {
  
}