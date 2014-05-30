package minirpg.util

import scalafx.scene.Node
import scalafx.animation.TranslateTransition
import scalafx.util.Duration

object Velocity {
  
  def components(speed : Double, direction : Double) =
    (speed * Math.cos(direction), speed * Math.sin(direction));
  
  def randVelocity(minSpeed : Double, maxSpeed : Double, minAngle : Double = 0.0, maxAngle : Double = Math.PI * 2.0) = {
    val speed = (maxSpeed - minSpeed) * Math.random + minSpeed;
    val direction = (maxAngle - minAngle) * Math.random + minAngle;
    components(speed, direction);
  }
  
  def randVelocities(minSpeed : Double, maxSpeed : Double, minAngle : Double = 0.0, maxAngle : Double = Math.PI * 2.0, num : Int) = 
    for (i <- 0 until num) yield randVelocity(minSpeed, maxSpeed, minAngle, maxAngle);
  
  def asTranslation(velocity : (Double, Double), duration : Duration, node : Node) = {
    new TranslateTransition(duration, node) {
      byX = velocity._1;
      byY = velocity._2;
    }
  }

}