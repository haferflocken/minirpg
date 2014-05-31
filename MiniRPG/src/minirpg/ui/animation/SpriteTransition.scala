package minirpg.ui.animation

import javafx.animation.Transition
import scalafx.scene.image.ImageView
import scalafx.util.Duration
import javafx.animation.Interpolator
import scalafx.geometry.Rectangle2D

class SpriteTransition(
    val imageView : ImageView,
    val duration : Duration,
    val frameWidth : Int,
    val frameHeight : Int,
    val columns : Int,
    val rows : Int) extends Transition {
  
  setCycleCount(-1);
  setCycleDuration(duration);
  setInterpolator(Interpolator.LINEAR);
  
  val length = columns * rows;
  private var lastIndex = -1;
  
  def interpolate(frac : Double) : Unit = {
    val index = Math.min((frac * length).toInt, length - 1);
    if (index != lastIndex) {
      val x = (index % columns) * frameWidth;
      val y = (index / columns) * frameHeight;
      imageView.viewport = new Rectangle2D(x, y, frameWidth, frameHeight);
      lastIndex = index;
    }
  };

}