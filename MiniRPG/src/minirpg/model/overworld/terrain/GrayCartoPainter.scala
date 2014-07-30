package minirpg.model.overworld.terrain

import scalafx.scene.paint.Color
import scalafx.scene.paint.Paint
import scala.Vector

object GrayCartoPainter extends TerrainPainter {
  
  val colors = Vector[(Double, Color)](
      (0.9, Color.gray(1.0)),
      (0.8, Color.gray(0.9)),
      (0.7, Color.gray(0.8)),
      (0.6, Color.gray(0.7)),
      (0.5, Color.gray(0.6)),
      (0.4, Color.gray(0.5)),
      (0.3, Color.gray(0.4)),
      (0.2, Color.gray(0.3)),
      (0.1, Color.gray(0.2)),
      (0.0, Color.gray(0.1)));
  
  def paintForLand(height : Double, gradient : (Double, Double)) : Paint = {
    for ((h, c) <- colors if height >= h)
      return c;
    return Color.BLACK;
  }
  
  def paintForWater(depth : Double, gradient : (Double, Double)) : Paint = Color.LIGHTSKYBLUE;
  
}
