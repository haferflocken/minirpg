package minirpg.util

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ArrayStack
import scala.collection.immutable.Queue
import scala.collection.mutable.PriorityQueue

/**
 * A node in a graph.
 */
class Graph[K, V](
    val id : K,
    var data : V,
    val connections : ArrayBuffer[Graph[K, V]] = new ArrayBuffer[Graph[K, V]],
    val weights : ArrayBuffer[Int] = new ArrayBuffer[Int]) {
  
  type Path = Queue[Graph[K, V]];

  /**
   * Connects this node to another node with a given edge weight.
   */
  def connectTo(g : Graph[K, V], weight : Int) : Unit = {
    val i = connections.indexOf(g);
    if (i == -1) {
      connections.append(g);
      weights.append(weight);
    }
    else {
      connections.update(i, g);
      weights.update(i, weight);
    }
  }
  
  /**
   * Applies a function to each node in this graph.
   */
  def foreach(f : (Graph[K, V]) => Unit) : Unit = {
    val stack : ArrayStack[Graph[K, V]] = new ArrayStack();
    stack.push(this);
    
    while (stack.nonEmpty) {
      val g = stack.pop();
      f(g);
      for (x <- g.connections)
        stack.push(x);
    }
  }
  
  /**
   * Returns a path from this node to the given node, or null if no path exists.
   */
  def findPath(end : Graph[K, V]) : Path = {
    val paths = new PathQueue[K, V];
    paths.enqueue(Queue(this), 0);
    
    while(paths.getPriorityAt(0) < Int.MaxValue) {
      val pair = paths.dequeue;
      val path = pair._1;
      val pathLength = pair._2;
      val last = path.last;
      val nextIndex = last.indexOfLightestConnection;
      val next = last.connections(nextIndex);
      if (next == end) {
        return path :+ next;
      }
      
      val otherPathIndex = paths.indexOf(next);
      if (otherPathIndex != -1) {
        val otherPath = paths.getValueAt(otherPathIndex);
        val otherPathLength = paths.getPriorityAt(otherPathIndex);
        if (pathLength < otherPathLength) {
          paths.removeFrom(otherPathIndex);
          paths.enqueue(otherPath, Int.MaxValue);
        }
        else
          paths.enqueue(path :+ next, Int.MaxValue);
      }
      else
        paths.enqueue(path :+ next, pathLength + last.weights(nextIndex));
    }
    
    return null;
  }
  
  def indexOfLightestConnection() : Int = {
    var minIndex = 0;
    for (i <- minIndex + 1 until weights.length) {
      if (weights(i) < weights(minIndex))
        minIndex = i;
    }
    return minIndex;
  }
  
  override def equals(o : Any) : Boolean = {
    if (!o.isInstanceOf[Graph[K, V]])
      return false;
    return id == o.asInstanceOf[Graph[K, V]].id;
  }
  
  override def hashCode : Int = id.hashCode;
  
  override def toString =
    s"id: $id, data: $data, connections: {" + connections.map(_.id).mkString(", ") + "}, weights: {" + weights.mkString(", ") + "}";
  
}

object Graph {
  
  def apply[K, V](data : V, rawCons : Map[K, Array[(K, Int)]]) : Map[K, Graph[K, V]] = {
    val gMap = rawCons.map((entry : (K, Array[(K, Int)])) => (entry._1, new Graph[K, V](entry._1, data)));
    connect(gMap, rawCons);
    return gMap;
  }
  
  def apply[K, V](rawData : Map[K, V], rawCons : Map[K, Array[(K, Int)]]) : Map[K, Graph[K, V]] = {
    val gMap = rawData.map((entry : (K, V)) => (entry._1, new Graph[K, V](entry._1, entry._2)));
    connect(gMap, rawCons);
    return gMap;
  }
  
  private def connect[K, V](gMap : Map[K, Graph[K, V]], rawCons : Map[K, Array[(K, Int)]]) = {
    for (e <- rawCons) {
      val g = e._1;
      for (c <- e._2) {
        gMap(g).connectTo(gMap(c._1), c._2);
      }
    }
  }
  
}