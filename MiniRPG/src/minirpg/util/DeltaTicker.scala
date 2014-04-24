package minirpg.util

import javafx.animation.AnimationTimer

class DeltaTicker(handler : (Long) => Unit) extends AnimationTimer {
  
  var lastTime : Long = 0;
  
  override def start() : Unit = {
    lastTime = System.currentTimeMillis();
    super.start();
  }

  def handle(now : Long) : Unit = {
    handler(now - lastTime);
    lastTime = now;
  }
  
}

trait Tickable {
  def tick(delta : Long) : Unit;
}