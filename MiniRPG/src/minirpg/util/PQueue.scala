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
  
  def nonEmpty : Boolean = queue.nonEmpty;
  
  override def toString = queue.toString;
  
  /**
   * A binary search to find where to put a priority.
   */
  protected def indexFor(priority : Int, start : Int = 0, end : Int = queue.length - 1) : Int = {
    if (end < start)
      return start;
    
    val check = (start + end)/2;
    val checkPriority = getPriorityAt(check);
    if (checkPriority > priority) {
      if (check > 0 && getPriorityAt(check - 1) < priority)
        return check;
      return indexFor(priority, start, check - 1); 
    }
    else if (checkPriority < priority) {
      if (check + 1 < queue.length && getPriorityAt(check + 1) > priority)
        return check + 1;
      return indexFor(priority, check + 1, end);
    }
    else
      return check;
  }
  
}

object PQueueTests extends App {
  val subject = new PQueue[String];
  
  subject.enqueue("10a", 10);
  subject.enqueue("1", 1);
  subject.enqueue("10b", 10);
  subject.enqueue("5", 5);
  
  println(subject);
}