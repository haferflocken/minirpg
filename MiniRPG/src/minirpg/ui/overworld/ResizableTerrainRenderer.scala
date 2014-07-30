package minirpg.ui.overworld

import scalafx.scene.canvas.GraphicsContext
import minirpg.ui.ResizableCanvas
import minirpg.model.overworld.terrain.Terrain

class ResizableTerrainRenderer(terrain : Terrain) extends ResizableCanvas.Renderer {
  
  // TODO make this work better with awkward numbers if possible.
  def draw(g : GraphicsContext, imageWidth : Double, imageHeight : Double) : Unit = {
    val tileWidth = imageWidth / terrain.width;
    val tileHeight = imageHeight / terrain.height;
    val intWidth = imageWidth.toInt;
    val intHeight = imageHeight.toInt;
    
    val landHeightRange = (terrain.maxHeight - terrain.waterLevel);
    val waterHeightRange = (terrain.waterLevel - terrain.minHeight);
    
    for (i <- 0 until terrain.width; j <- 0 until terrain.height) {
      if (terrain.grid(i)(j) > terrain.waterLevel) {
        val percHeight = (terrain.grid(i)(j) - terrain.waterLevel) / landHeightRange;
        g.fill = terrain.painter.paintForLand(percHeight, terrain.gradient(i)(j));
      }
      else {
        val percDepth = (terrain.waterLevel + terrain.grid(i)(j)) / waterHeightRange;
        g.fill = terrain.painter.paintForWater(percDepth, terrain.gradient(i)(j));
      }
      val x = i * intWidth / terrain.width;
      val y = j * intHeight / terrain.height;
      g.fillRect(x, y, tileWidth, tileHeight);
    }
  };
  
}