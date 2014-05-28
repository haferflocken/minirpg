package minirpg.collection.mutable

import scala.collection.mutable.ArrayBuffer
import scala.annotation.tailrec

class PQueue[E] {
  
  private val queue : ArrayBuffer[(E, Int)] = new ArrayBuffer;
  
  def +=(pair : (E, Int)) : Int = {
    val i = indexFor(pair._2);
    queue.insert(i, pair);
    return i;
  }
  
  def ++=(es : Iterable[(E, Int)]) : Unit = queue ++= es;
  
  def -=(value : E) : Boolean = {
    val i = indexOf(value);
    if (i != -1 && getValueAt(i) == value) {
      removeFrom(i);
      return true;
    }
    return false;
  }
  
  def dequeue() : (E, Int) = queue.remove(0);
  
  def filter(f : (E) => Boolean) = queue.map(_._1).filter(f);
  
  def filterPairs(f : ((E, Int)) => Boolean) = queue.filter(f);
  
  def foreach(f : ((E, Int)) => Unit) : Unit = queue.foreach(f);
  
  def getPriorityAt(index : Int) : Int = queue(index)._2;
  
  def getPriorityOf(value : E) : Int = getPriorityAt(indexOf(value));
  
  def getValueAt(index : Int) = queue(index)._1;
  
  def getValueOf(priority : Int) = getValueAt(indexFor(priority));
  
  def removeFrom(index : Int) = queue.remove(index);
  
  def indexOf(value : E) : Int = {
    for (i <- 0 until queue.length) {
      if (value == getValueAt(i))
        return i;
    }
    return -1;
  }
  
  def nonEmpty : Boolean = queue.nonEmpty;
  
  def contains(e : E) : Boolean = queue.find(_._1 equals e).nonEmpty;
  
  def mkString(str : String) = queue.mkString(str);
  
  override def toString = queue.toString;
  
  /**
   * A binary search to find where to put a priority.
   */
  @tailrec protected final def indexFor(priority : Int, start : Int = 0, end : Int = queue.length - 1) : Int = {
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

object PQueue {
  
  def apply[E](elems : Iterable[(E, Int)]) : PQueue[E] = {
    val out = new PQueue[E];
    out ++= elems;
    return out;
  };
  
}