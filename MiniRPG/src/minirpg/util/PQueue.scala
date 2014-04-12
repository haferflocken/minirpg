package minirpg.util

import scala.collection.mutable.ArrayBuffer

class PQueue[E] {
  
  private val queue : ArrayBuffer[(E, Int)] = new ArrayBuffer;
  
  def enqueue(value : E, priority : Int) : Int = {
    val i = indexFor(priority);
    queue.insert(i, (value, priority));
    return i;
  }
  
  def dequeue() : (E, Int) = queue.remove(0);
  
  def getPriorityAt(index : Int) : Int = queue(index)._2;
  
  def getPriorityOf(value : E) : Int = getPriorityAt(indexOf(value));
  
  def getValueAt(index : Int) = queue(index)._1;
  
  def getValueOf(priority : Int) = getValueAt(indexFor(priority));
  
  def removeFrom(index : Int) = queue.remove(index);
  
  def remove(value : E) : Boolean = {
    val i = indexOf(value);
    if (i != -1 && getValueAt(i) == value) {
      removeFrom(i);
      return true;
    }
    return false;
  }
  
  /*def update(index : Int, value : E, priority : Int) = {
    removeFrom(index);
    enqueue(value, priority);
  }*/
  
  def indexOf(value : E) : Int = {
    for (i <- 0 until queue.length) {
      if (value == getValueAt(i))
        return i;
    }
    return -1;
  }
  
  /**
   * A binary search to find where to put a priority.
   */
  protected def indexFor(priority : Int, start : Int = 0, end : Int = queue.length) : Int = {
    var check = (start + end)/2;
    if (check == start || priority == getPriorityAt(check))
      return check;
    if (priority < getPriorityAt(check))
      return indexFor(priority, start, check);
    return indexFor(priority, check, end);
  }
  
}