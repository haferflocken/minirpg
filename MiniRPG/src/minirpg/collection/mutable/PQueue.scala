package minirpg.collection.mutable

import scala.collection.mutable
import scala.annotation.tailrec

class PQueue[E](initialCapacity : Int = 16) {
  
  private val queue = new mutable.ArrayBuffer[(E, Int)](initialCapacity);
  private val elems = new mutable.HashMap[E, Int];
  
  def +=(pair : (E, Int)) : Int = {
    val i = indexFor(pair._2);
    queue.insert(i, pair);
    
    val num = elems.getOrElse(pair._1, 0);
    elems.update(pair._1, num + 1);
    return i;
  }
  
  def ++=(es : Iterable[(E, Int)]) : Unit = {
    for (pair <- es)
      this += pair;
  }
  
  def -=(value : E) : Boolean = {
    val i = indexOf(value);
    if (i != -1 && getValueAt(i) == value) {
      removeFrom(i);
      return true;
    }
    return false;
  }
  
  def dequeue() : (E, Int) = removeFrom(0);
  
  def filter(f : (E) => Boolean) = queue.map(_._1).filter(f);
  
  def filterPairs(f : ((E, Int)) => Boolean) = queue.filter(f);
  
  def foreach(f : ((E, Int)) => Unit) : Unit = queue.foreach(f);
  
  def getPriorityAt(index : Int) : Int = queue(index)._2;
  
  def getPriorityOf(value : E) : Int = getPriorityAt(indexOf(value));
  
  def getValueAt(index : Int) = queue(index)._1;
  
  def getValueOf(priority : Int) = getValueAt(indexFor(priority));
  
  def removeFrom(index : Int) : (E, Int) = {
    val pair = queue.remove(index);
    if (pair != null) {
      val num = elems(pair._1);
      if (num > 1)
        elems.update(pair._1, num - 1);
      else
        elems.remove(pair._1);
      
      return pair;
    }
    return null;
  }
  
  def indexOf(value : E) : Int = {
    if (!elems.contains(value))
      return -1;
    
    for (i <- 0 until queue.length) {
      if (value == getValueAt(i))
        return i;
    }
    return -1;
  }
  
  def nonEmpty : Boolean = queue.nonEmpty;
  
  def contains(e : E) : Boolean = elems.contains(e);
  
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
    val out = new PQueue[E](elems.size * 2);
    out ++= elems;
    return out;
  };
  
}