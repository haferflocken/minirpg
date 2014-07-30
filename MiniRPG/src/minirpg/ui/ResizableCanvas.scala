package minirpg.ui

import collection.mutable
import scalafx.scene.Node
import scalafx.delegate.SFXDelegate
import scalafx.beans.property.DoubleProperty
import scalafx.scene.canvas.GraphicsContext
import scalafx.Includes._

class ResizableJFXCanvas extends javafx.scene.canvas.Canvas {
  import javafx.beans.value.ChangeListener
  import javafx.scene.canvas.GraphicsContext
  import javafx.beans.value.ObservableValue
  
  private val sizeListener = new ChangeListener[Any] {
    def changed(observable : ObservableValue[_], oldValue : Any, newValue : Any) : Unit = redraw;
  }
  widthProperty.addListener(sizeListener);
  heightProperty.addListener(sizeListener);
  
  var renderer : ResizableCanvas.Renderer = null;
  
  def redraw : Unit = {
    if (renderer != null)
        renderer.draw(getGraphicsContext2D, getWidth, getHeight);
  };
  
  override def isResizable() = true;
  override def prefWidth(width : Double) = getWidth;
  override def prefHeight(height : Double) = getHeight;

}

class ResizableCanvas(override val delegate : ResizableJFXCanvas = new ResizableJFXCanvas)
  extends Node(delegate) with SFXDelegate[ResizableJFXCanvas] {
  
  def renderer = delegate.renderer;
  
  def renderer_=(v : ResizableCanvas.Renderer) = delegate.renderer = v;
  
  def redraw : Unit = delegate.redraw;
    
  def height: DoubleProperty = delegate.heightProperty;
  
  def height_=(v: Double) : Unit = height() = v;

  def width: DoubleProperty = delegate.widthProperty;
  
  def width_=(v: Double) : Unit = width() = v;

  def graphicsContext2D: GraphicsContext = delegate.getGraphicsContext2D;
  
}

object ResizableCanvas {
  implicit def sfxResizableCanvas2jfx(v: ResizableCanvas) = v.delegate;
  
  abstract class Renderer {
    def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit;
  }
  
  class LayeredRenderer extends Renderer {
    val layers = new mutable.ArrayBuffer[Renderer];
    
    def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit = {
      for (painter <- layers)
        painter.draw(g, canvasWidth, canvasHeight);
    };
  }
}