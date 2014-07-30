package minirpg.ui.overworld

import scalafx.scene.canvas.GraphicsContext
import minirpg.ui.ResizableCanvas
import scalafx.scene.paint.Color
import minirpg.model.world.World
import minirpg.model.Region
import minirpg.model.overworld.Landmark

class ResizableWorldRegionRenderer(worlds : Map[World, (Region, Vector[Landmark])], overworldWidth : Int, overworldHeight : Int)
  extends ResizableCanvas.Renderer {
  
  def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit = {
    val halfTileWidth = canvasWidth.toDouble / overworldWidth / 2;
    val halfTileHeight = canvasHeight.toDouble / overworldHeight / 2;
    
    g.stroke = Color.BLUE;
    g.fill = Color.WHITE;
    for ((w, (r, ls)) <- worlds) {
      // Fill the background of the polygon.
      val polyX = ls.map(l => l.x.toDouble).toArray;
      val polyY = ls.map(l => l.y.toDouble).toArray;
      g.fillPolygon(polyX, polyY, ls.size);
      
      // Draw the lines of the polygon.
      for (i <- 0 until ls.length; j <- i + 1 until ls.length) {
        val l1 = ls(i);
        val l2 = ls(j);
        val l1X = l1.x * canvasWidth / overworldWidth + halfTileWidth;
        val l1Y = l1.y * canvasHeight / overworldHeight + halfTileHeight;
        val l2X = l2.x * canvasWidth / overworldWidth + halfTileWidth;
        val l2Y = l2.y * canvasHeight / overworldHeight + halfTileHeight;
        g.strokeLine(l1X, l1Y, l2X, l2Y);
      }
    }
  };
  
}