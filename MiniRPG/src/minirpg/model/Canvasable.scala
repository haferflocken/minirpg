package minirpg.model

import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.SnapshotParameters

trait Canvasable {
  
  def mkCanvas(imageWidth : Int, imageHeight : Int) : Canvas;
  
  def mkImage(imageWidth : Int, imageHeight : Int) : Image = {
    val canvas = mkCanvas(imageWidth, imageHeight);
    return canvas.snapshot(new SnapshotParameters, new WritableImage(imageWidth, imageHeight));
  }

}