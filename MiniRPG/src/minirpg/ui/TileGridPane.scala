package minirpg.ui

import javafx.scene.{ layout => jfxsl }
import javafx.{ geometry => jfxg }
import javafx.{ scene => jfxs }
import scalafx.delegate.SFXDelegate
import scalafx.scene.layout.Pane
import scalafx.scene.Node

class TileGridJFXPane(val gridColumns : Int, val gridRows : Int) extends jfxsl.Pane {
  import javafx.beans.value.ChangeListener
  import javafx.beans.value.ObservableValue
  import TileGridJFXPane._
  
  widthProperty.addListener(new ChangeListener[Any] {
    def changed(observable : ObservableValue[_], oldValue : Any, newValue : Any) : Unit =
      tileWidth = getWidth / gridColumns;
    });
  heightProperty.addListener(new ChangeListener[Any] {
    def changed(observable : ObservableValue[_], oldValue : Any, newValue : Any) : Unit =
      tileHeight = getHeight / gridRows;
    });

  private var tileWidth = getWidth / gridColumns;
  private var tileHeight = getHeight / gridRows;
  
  def add(child : jfxs.Node, col : Int, row : Int) : Unit = {
    setConstraint(child, COLUMN_PROPERTY, new Integer(col));
    setConstraint(child, ROW_PROPERTY, new Integer(row));
    getChildren.add(child);
  };
  
  override protected def layoutChildren() : Unit = {
    val nodes = getManagedChildren[jfxs.Node]();
    for (i <- 0 until nodes.size()) {
      val node = nodes.get(i);
      val col = node.getProperties().get(COLUMN_PROPERTY).asInstanceOf[Integer];
      val row = node.getProperties().get(ROW_PROPERTY).asInstanceOf[Integer];
      node.relocate(col * tileWidth, row * tileHeight);
    }
  };
  
}

object TileGridJFXPane {
  
  protected val COLUMN_PROPERTY = "tilegridpane-column";
  protected val ROW_PROPERTY = "tilegridpane-row";
  
  def setConstraint(node : jfxs.Node, key : AnyRef, value : AnyRef) : Unit = {
    if (value == null) {
      node.getProperties().remove(key);
    } else {
      node.getProperties().put(key, value);
    }
    if (node.getParent() != null) {
      node.getParent().requestLayout();
    }
  };
  
}

class TileGridPane(override val delegate : TileGridJFXPane)
    extends Pane(delegate) with SFXDelegate[TileGridJFXPane] {
  
  def this(gridCols : Int, gridRows : Int) = this(new TileGridJFXPane(gridCols, gridRows));
  
  def gridColumns = delegate.gridColumns;
  
  def gridRows = delegate.gridRows;
  
  def add(child : Node, col : Int, row : Int) = delegate.add(child, col, row);
  
}