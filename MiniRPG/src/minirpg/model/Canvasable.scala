package minirpg.model

import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.SnapshotParameters
import minirpg.ui.ResizableCanvas

trait Canvasable {
  
  def mkResizableCanvas : ResizableCanvas;

}