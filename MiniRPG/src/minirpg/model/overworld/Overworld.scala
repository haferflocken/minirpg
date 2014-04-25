package minirpg.model.overworld

import scalafx.scene.image.Image
import scalafx.scene.canvas.Canvas
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.WritableImage

class Overworld(val terrain : Terrain, val landmarks : Vector[Landmark]) {
  
  val roads = null;
  
  def mkCanvas(imageWidth : Int, imageHeight : Int) : Canvas = {
    val canvas = terrain.mkCanvas(imageWidth, imageHeight);
    
    // TODO Place landmarks on the canvas.
    
    // TODO Draw roads between the landmarks.
    
    return canvas;
  }
  
  def mkImage(imageWidth : Int, imageHeight : Int) : Image = {
    val canvas = mkCanvas(imageWidth, imageHeight)
    return canvas.snapshot(new SnapshotParameters, new WritableImage(imageWidth, imageHeight));
  }

}

object Overworld {
  
  def mkRandomOverworld(width : Int, height : Int) : Overworld = {
    val t = Terrain.mkRandomTerrain(width max height, 100.0, 0.0).crop(0, 0, width, height);
    
    
    return new Overworld(t, null);
  }
  
}