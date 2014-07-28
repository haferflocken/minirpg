package minirpg.ui

import collection.mutable
import javafx.beans.value.ChangeListener
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.beans.value.ObservableValue

class ResizableCanvas extends Canvas {
  
  val layers = new mutable.ArrayBuffer[ResizableCanvas.ResizableLayer];
  
  private val sizeListener = new ChangeListener[Any] {
    def changed(observable : ObservableValue[_], oldValue : Any, newValue : Any) : Unit = redraw;
  }
  widthProperty.addListener(sizeListener);
  heightProperty.addListener(sizeListener);
  
  private def redraw : Unit = {
    val g = getGraphicsContext2D();
    val width = getWidth;
    val height = getHeight;
    
    g.clearRect(0, 0, width, height);
    for (layer <- layers) {
      layer.draw(g, width, height);
    }
  };
  
  override def isResizable() = true;
  override def prefWidth(width : Double) = getWidth;
  override def prefHeight(height : Double) = getHeight;

}

object ResizableCanvas {
  
  abstract class ResizableLayer {
    
    def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit;
    
    def isClickableAt(mouseX : Double, mouseY : Double, canvasWidth : Double, canvasHeight : Double) : Boolean;
    
  }
  
}