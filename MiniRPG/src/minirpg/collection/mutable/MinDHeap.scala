package minirpg.collection.mutable

import collection.mutable
import scala.annotation.tailrec

/**
 * A d-ary min heap which stores data of type E with integer priorities.
 * 
 * Implementation details:
 *    Implementation based off of http://en.wikipedia.org/wiki/D-ary_heap
 *    Priorities are stored in an int array to avoid boxing.
 * 
 * @author John Werner
 */
class MinDHeap[E](val d : Int, initialCapacity : Int = 16) {
  
  private val elems = new mutable.ArrayBuffer[E](initialCapacity);
  private var priorities = new Array[Int](initialCapacity);
  private var _size = 0;
  
  private def ensureCapacity : Unit = {
    if (_size == priorities.length) {
      val newPriorities = new Array[Int](priorities.length * 2);
      for (i <- 0 until _size)
        newPriorities(i) = priorities(i);
      priorities = newPriorities;
    }
  };
  
  private def swap(i : Int, j : Int) : Unit = {
    val e = elems(i);
    val p = priorities(i);
    elems(i) = elems(j);
    priorities(i) = priorities(j);
    elems(j) = e;
    priorities(j) = p;
  };
  
  @tailrec private def swapDown(i : Int) : Unit = {
    val min = minChildOf(i);
    if (min == -1)
      return;
    if (priorities(i) > priorities(min))
      swap(i, min);
    swapDown(min);
  };
  
  @tailrec private def swapUp(i : Int) : Unit = {
    if (i == 0)
      return;
    val parent = parentOf(i);
    if (priorities(parent) > priorities(i))
      swap(i, parent);
    swapUp(parent);
  };
  
  @inline private def parentOf(i : Int) = (i - 1) / d;
  
  @inline private def jthChildOf(i : Int, j : Int) = d * i + j;
  
  private def minChildOf(i : Int) : Int = {
    val firstChild = jthChildOf(i, 1);
    if (firstChild < _size) {
      var out = firstChild;
      for (c <- firstChild + 1 until firstChild + d) {
        if (priorities(out) > priorities(c))
          out = c;
      }
      return out;
    }
    return -1;
  };
  
  /**
   * Add an element e with priority p to the heap.
   */
  def add(e : E, p : Int) : Unit = {
    // Add to the end.
    ensureCapacity;
    elems += e;
    priorities(_size) = p;
    _size += 1;
    
    // Swap up.
    swapUp(_size - 1);
  };
  
  /**
   * Change the priority of the first element == e in the heap to p.
   */
  def updatePriority(e : E, p : Int) : Unit = {
    val i = elems.indexOf(e);
    val oldP = priorities(i);
    priorities(i) = p;
    if (p < oldP)
      swapDown(i);
    else
      swapUp(i);
  };
  
  /**
   * Remove the min element-priority pair from the heap and return it.
   */
  def deleteMin : (E, Int) = {
    // Grab the first item, move the last item to the front, and decrease _size.
    val out = (elems(0), priorities(0));
    _size -= 1;
    elems(0) = elems(_size);
    priorities(0) = priorities(_size);
    elems.remove(_size);
    
    // Swap down to fix the heap.
    swapDown(0);
    
    // Return.
    return out;
  };
  
  /**
   * Get the min element-priority pair in the heap without removing it.
   */
  def peekMin : (E, Int) = (elems(0), priorities(0));
  
  /**
   * Get the min element in the heap without removing it.
   */
  def peekMinElement : E = elems(0);
  
  /**
   * Get the min priority in the heap without removing it.
   */
  def peekMinPriority : Int = priorities(0);
  
  /**
   * Returns the number of elements in the heap.
   */
  def size = _size;
  
  /**
   * True if size > 0; false otherwise.
   */
  def nonEmpty = _size > 0;
  
  /**
   * True if size == 0; false otherwise.
   */
  def isEmpty = _size == 0;
  
  /**
   * Return the height of the internal tree.
   */
  def height = (Math.log(_size) / Math.log(d)).toInt + 1;
  
}

/*import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.layout.TilePane
import scalafx.geometry.Orientation
import scalafx.scene.text.Text
import scalafx.scene.paint.Color
import scalafx.geometry.Pos
import scalafx.scene.layout.StackPane
object MinDHeapTests extends JFXApp {
  import scalafx.Includes._
  
  val heap = new MinDHeap[String](2);
  
  for (i <- 0 until 15) {
    val x = (Math.random * 100).toInt;
    heap.add(String.valueOf(x), x);
  }
  
  val height = heap.height;
  val rows = for (i <- 0 until height) yield Math.pow(heap.d, i).toInt;
  
  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    resizable = false;
    scene = new Scene {
      content = new VBox {
        prefWidth = 800;
        prefHeight = 600;
        alignment = Pos.CENTER;
        
        val (elems, priorities) = heap.getInternals;
        var i = 0;
        for (r <- rows) {
          println(s"Adding row of size $r");
          val rowPane = new TilePane {
            prefColumns = r;
            prefWidth = 800;
            alignment = Pos.CENTER;
            orientation = Orientation.HORIZONTAL;
          };
          for (j <- 0 until r) {
            if (i < heap.size) {
              val e = elems(i);
              val p = priorities(i);
              val display = new Text(s"($e @ $p)") {
                fill = Color.BLACK;
              };
              val displayPane = new StackPane {
                minWidth = 800 / r;
                content = display;
                alignment = Pos.CENTER;
              }
              rowPane.children.add(displayPane);
              i += 1;
            }
          }
          children.add(rowPane);
        }
      }
    }
  }
  
}*/