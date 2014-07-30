package minirpg.ui.overworld

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Paint
import scalafx.scene.paint.Color
import minirpg.ui.ResizableCanvas

class ResizableRoadRenderer(road : Vector[(Int, Int)], overworldWidth : Int, overworldHeight : Int) extends ResizableCanvas.Renderer {
  
  var fill : Paint = Color.RED;
  
  def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit = {
    val tileWidth = canvasWidth.toDouble / overworldWidth;
    val tileHeight = canvasHeight.toDouble / overworldHeight;
    
    g.fill = fill;
    for (j <- 0 until road.length) {
      val p = road(j);
      val rX = p._1 * canvasWidth / overworldWidth;
      val rY = p._2 * canvasHeight / overworldHeight;
      g.fillRect(rX, rY, tileWidth, tileHeight);
    }
  };
  
}