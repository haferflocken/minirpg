package minirpg.util

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ArrayStack
import scala.collection.immutable.Queue
import scala.collection.mutable.PriorityQueue
import scala.collection.mutable.ImmutableMapAdaptor
import scala.collection.mutable.HashMap

/**
 * A node in a graph.
 */
class Graph[K, V](
    val id : K,
    var data : V,
    val connections : ArrayBuffer[Graph[K, V]] = new ArrayBuffer[Graph[K, V]],
    val weights : ArrayBuffer[Int] = new ArrayBuffer[Int]) {

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
    return hashCode == o.asInstanceOf[Graph[K, V]].hashCode;
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
  
  /**
   * Find the shortest path through a graph using Dijkstra's Algorithm.
   * Adapted from pseudocode courtesy of Wikipedia.
   */
  def findPath[K, V](startId : K, endId : K, nodes : Map[K, Graph[K, V]]) : Queue[Graph[K, V]] = {
    val paths = findPaths(startId, Vector(endId), nodes);
    if (paths.nonEmpty)
      return paths(0);
    return null;
  }
  
  def findPaths[K, V](startId : K, endIds : Vector[K], nodes : Map[K, Graph[K, V]]) : Vector[Queue[Graph[K, V]]] = {
    val startNode = nodes.getOrElse(startId, null);
    if (startNode == null) {
      println("No path: failed to get start node.");
      return Vector();
    }
    
    val endNodes = endIds.map(nodes.getOrElse(_, null)).filter(_ != null).toBuffer;
    if (endNodes.length == 0) {
      println("No path: failed to get end nodes.");
      return Vector();
    }
    
    var pq = new PQueue[Graph[K, V]];
    val dist = new HashMap[Graph[K, V], Int];
    val previous = new HashMap[Graph[K, V], Graph[K, V]];
    val outPaths = new ArrayBuffer[Queue[Graph[K, V]]];
    
    nodes.foreach((e : (K, Graph[K, V])) => dist.put(e._2, Int.MaxValue));
    dist.update(startNode, 0);
    dist.foreach((e : (Graph[K, V], Int)) => pq.enqueue(e._1, e._2));
    
    while (pq.nonEmpty) {
      val uPair = pq.dequeue;
      val u = uPair._1;
      val uDist = uPair._2;
      
      // If we have found a path we are looking for, save it.
      if (endNodes.contains(u)) {
        var outPath = Queue[Graph[K, V]]();
        var outU = u;
        while (previous.contains(outU)) {
          // Detect infinite loops. TODO Prevent these from happening.
          if (outPath contains outU) {
            println(s"Cycle detected at $outU. Quitting while we're ahead.");
            return outPaths.toVector;
          }
          outPath = outU +: outPath;
          outU = previous(outU);
        }
        outPaths += outPath;
        endNodes -= u;
        
        // If all paths have been found, we're done.
        if (outPaths.length == endIds.length)
          return outPaths.toVector;
      }
      
      for (i <- 0 until u.connections.length) {
        val v = u.connections(i);
        val alt = uDist + u.weights(i);
        if (alt < dist(v)) {
          dist.update(v, alt);
          previous.update(v, u);
          pq.remove(v);
          pq.enqueue(v, alt);
        }
      }
    }
    
    return outPaths.toVector;
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