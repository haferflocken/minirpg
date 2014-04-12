package minirpg.util

import scala.collection.immutable.Queue
import scala.collection.mutable.HashMap

class PathQueue[K, V] extends PQueue[Queue[Graph[K, V]]] {
  
  private val lastMap : HashMap[Graph[K, V], Int] = new HashMap;

  override def enqueue(value : Queue[Graph[K, V]], priority : Int) : Int = {
    val i = super.enqueue(value, priority);
    lastMap.put(value.last, i);
    return i;
  }
  
  override def dequeue() : (Queue[Graph[K, V]], Int) = {
    val out = super.dequeue();
    lastMap.remove(out._1.last);
    return out;
  }
  
  override def removeFrom(index : Int) = {
    val out = super.removeFrom(index);
    lastMap.remove(out._1.last);
    out;
  }
  
  override def remove(value : Queue[Graph[K, V]]) : Boolean = {
    val success = super.remove(value);
    if (success) lastMap.remove(value.last);
    success;
  }
  
  def indexOf(last : Graph[K, V]) = lastMap.getOrElse(last, -1);
}