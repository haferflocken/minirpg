package minirpg.model.overworld

import scalafx.scene.paint.Color
import scalafx.scene.paint.Paint

object HueCartoPainter extends TerrainPainter {
  
  val colors = Vector[(Double, (Double, Double))](
      (0.9, (0, 1.0)),
      (0.8, (30, 1.0)),
      (0.7, (60, 1.0)),
      (0.6, (90, 1.0)),
      (0.5, (120, 1.0)),
      (0.4, (150, 1.0)),
      (0.3, (180, 1.0)),
      (0.2, (210, 1.0)),
      (0.1, (240, 1.0)));
  
  def hb(height : Double) : (Double, Double) = {
    for ((h, hb) <- colors if height >= h)
      return hb;
    return (300.0, 1.0);
  }
  
  def paintForLand(height : Double, gradient : (Double, Double)) : Paint = {
    val (hue, brightness) = hb(height);
    return Color.hsb(hue, Math.sqrt(1.0 - Math.max(gradient._1, gradient._2)), brightness);
  }
  
  def paintForWater(depth : Double, gradient : (Double, Double)) : Paint = Color.LIGHTGRAY;
  
}