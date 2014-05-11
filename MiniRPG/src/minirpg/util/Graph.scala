package minirpg.util

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ArrayStack
import scala.collection.immutable.Queue
import scala.collection.mutable.PriorityQueue
import scala.collection.mutable.ImmutableMapAdaptor
import scala.collection.mutable.HashMap
import scala.collection.concurrent.TrieMap
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

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
  
  def verboseToString =
    s"id: $id, data: $data, connections: {" + connections.map(_.id).mkString(", ") + "}, weights: {" + weights.mkString(", ") + "}";
  
  
  override def equals(o : Any) : Boolean = {
    if (!o.isInstanceOf[Graph[K, V]])
      return false;
    return id == o.asInstanceOf[Graph[K, V]].id;
  }
  
  override def hashCode : Int = id.hashCode;
  
  override def toString = id.toString;
  
}

object Graph {
  
  /**
   * Construct a graph, giving each node the same data.
   */
  def apply[K, V](data : V, rawCons : Map[K, Array[(K, Int)]]) : Map[K, Graph[K, V]] = {
    val gMap = rawCons.map((entry : (K, Array[(K, Int)])) => (entry._1, new Graph[K, V](entry._1, data)));
    connect(gMap, rawCons);
    return gMap;
  }
  
  /**
   * Construct a graph.
   */
  def apply[K, V](rawData : Map[K, V], rawCons : Map[K, Array[(K, Int)]]) : Map[K, Graph[K, V]] = {
    val gMap = rawData.map((entry : (K, V)) => (entry._1, new Graph[K, V](entry._1, entry._2)));
    connect(gMap, rawCons);
    return gMap;
  }
  
  /**
   * Make the connections in a graph.
   */
  private def connect[K, V](gMap : Map[K, Graph[K, V]], rawCons : Map[K, Array[(K, Int)]]) = {
    for (e <- rawCons) {
      val g = e._1;
      for (c <- e._2) {
        gMap(g).connectTo(gMap(c._1), c._2);
      }
    }
  }
  
  /**
   * Find the shortest path through a graph using Dijkstra's Algorithm.
   */
  def findPath[K, V](startId : K, endId : K, nodes : Map[K, Graph[K, V]]) : Queue[Graph[K, V]] = {
    val paths = findPaths(startId, Vector(endId), nodes);
    if (paths.nonEmpty)
      return paths(endId);
    return null;
  }
  
  /**
   * Find the shortest paths from the start to the ends using Dijkstra's Algorithm.
   * Adapted from pseudocode courtesy of Wikipedia.
   * Returns a map of endId -> path.
   */
  def findPaths[K, V](startId : K, endIds : Iterable[K], nodes : Map[K, Graph[K, V]]) : Map[K, Queue[Graph[K, V]]] = {
    val startNode = nodes.getOrElse(startId, null);
    if (startNode == null) {
      println(s"No paths from $startId: failed to get start node.");
      return Map();
    }
    
    val endNodes = endIds.map(nodes.getOrElse(_, null)).filter(_ != null).toBuffer;
    if (endNodes.length == 0) {
      println(s"No paths from $startId: failed to get end nodes for " + endIds.mkString(", ") + ".");
      return Map();
    }
    
    val pq = new PQueue[Graph[K, V]];
    val dist = new HashMap[Graph[K, V], Int];
    val previous = new HashMap[Graph[K, V], Graph[K, V]];
    val outPaths = new HashMap[K, Queue[Graph[K, V]]];
    
    for ((key, node) <- nodes) dist(node) = Int.MaxValue;
    dist(startNode) = 0;
    for ((node, distance) <- dist) pq.enqueue(node, distance);
    
    while (pq.nonEmpty) {
      val (u, uDist) = pq.dequeue;
      
      // If we have found a path we are looking for, save it.
      if (endNodes.contains(u)) {
        var outPath = Queue[Graph[K, V]]();
        var outU = u;
        while (previous.contains(outU)) {
          // Detect infinite loops. TODO Prevent these from happening.
          if (outPath contains outU) {
            println(s"Cycle detected while finding path from $startNode to $u at $outU.");
            println("End points that will be skipped: " + endNodes.mkString(", ") + ".");
            return outPaths.toMap;
          }
          outPath = outU +: outPath;
          outU = previous(outU);
        }
        outPaths.update(u.id, outPath);
        endNodes -= u;
        
        // If all paths have been found, we're done.
        if (outPaths.size == endIds.size)
          return outPaths.toMap;
      }
      
      for (i <- 0 until u.connections.length) {
        val v = u.connections(i);
        val alt = uDist + u.weights(i);
        if (alt < dist(v)) {
          dist(v) = alt;
          previous(v) = u;
          pq.remove(v);
          pq.enqueue(v, alt);
        }
      }
    }
    
    return outPaths.toMap;
  }
  
  /**
   * Find the shortest paths from start points to end points.
   * Returns a map of (startId, endId) -> path.
   */
  def findPaths[K, V](endpointIds : Map[K, Iterable[K]], nodes : Map[K, Graph[K, V]]) : Map[(K, K), Queue[Graph[K, V]]] = {
    val out = new TrieMap[(K, K), Queue[Graph[K, V]]];
    
    val startTime = System.currentTimeMillis;
    
    val futures = new ArrayBuffer[Future[_]];
    for ((startId, endIds) <- endpointIds if endIds.nonEmpty) {
      val f : Future[Map[K, Queue[Graph[K, V]]]] = future {
        findPaths(startId, endIds, nodes);
      }
      f onSuccess {
        case paths => for (e <- endIds if paths contains e) out((startId, e)) = paths(e);
      }
      futures += f;
    }
    
    for (f <- futures) Await.ready(f, Duration.Inf);
    
    val endTime = System.currentTimeMillis;
    println("Found paths in " + (endTime - startTime) + " miliseconds.");
    
    return out.toMap;
  }
  
}