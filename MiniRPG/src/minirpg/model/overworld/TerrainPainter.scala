package minirpg.model.overworld

import scalafx.scene.paint.Paint
import scalafx.scene.paint.Color

trait TerrainPainter {
  
  val waterPaint : Paint;
  
  def paintFor(height : Double, gradient : (Double, Double), waterLevel : Double) : Paint;

}
