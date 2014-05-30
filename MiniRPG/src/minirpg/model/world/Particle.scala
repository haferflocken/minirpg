package minirpg.model.world

import scalafx.scene.Node
import scalafx.scene.Group

abstract class AbstractParticle {
  
  val world : World;
  val node : Node;

}

class Particle(
    override val world : World, 
    override val node : Node)
    extends AbstractParticle {
  
}