package minirpg.collection.immutable

import scala.collection.mutable
import scala.collection.immutable.Queue
import scala.collection.concurrent.TrieMap
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import minirpg.collection.mutable.PQueue

/**
 * An immutable graph.
 */
class Graph[K](val nodes : Set[K], val connections : Map[K, Map[K, Int]]) {
  
  /**
   * Figure out the sub-graphs of this graph in order to speed up pathfinding.
   */
  val subGraphs : Vector[Set[K]] = {
    var out = Vector[Set[K]]();
    
    // Search from nodes.
    for (n <- nodes) {
      if (out.forall(s => !s.contains(n))) {
        // If we don't yet have a sub-graph containing n, make one.
        val stack = new mutable.ArrayStack[K];
        val visited = new mutable.HashSet[K];
        
        stack += n;
        while (stack.nonEmpty) {
          val i = stack.pop;
          visited += i;
          val cons = connections.getOrElse(i, Nil);
          for ((e, w) <- cons) if (!visited.contains(e))
            stack += e;
        }
        
        println("Found a sub-graph of size " + visited.size + ".");
        out = out :+ visited.toSet;
      }
    }
    
    println("Found " + out.length + " sub-graphs.");
    out;
  };
  
  /**
   * Find the shortest path through a graph using Dijkstra's Algorithm.
   */
  def findPath(startId : K, endId : K) : Queue[K] = {
    val paths = findPaths(startId, Vector(endId));
    if (paths.nonEmpty)
      return paths(endId);
    return null;
  }
  
  /**
   * Find the shortest paths from the start to the ends using Dijkstra's Algorithm.
   * Adapted from pseudocode courtesy of Wikipedia.
   * Returns a map of endId -> path.
   */
  def findPaths(startId : K, endIds : Iterable[K]) : Map[K, Queue[K]] = {
    val startNode = startId;
    if (startNode == null) {
      println(s"No paths from $startId: failed to get start node.");
      return Map();
    }
    
    val endNodes = endIds.filter(_ != null).toBuffer;
    if (endNodes.length == 0) {
      println(s"No paths from $startId: failed to get end nodes for " + endIds.mkString(", ") + ".");
      return Map();
    }
    
    val pq = new PQueue[K];
    val dist = new mutable.HashMap[K, Int];
    val previous = new mutable.HashMap[K, K];
    val outPaths = new mutable.HashMap[K, Queue[K]];
    
    for (key <- nodes) dist(key) = Int.MaxValue;
    dist(startNode) = 0;
    for ((node, distance) <- dist) pq.enqueue(node, distance);
    
    while (pq.nonEmpty) {
      val (u, uDist) = pq.dequeue;
      
      // If we have found a path we are looking for, save it.
      if (endNodes.contains(u)) {
        var outPath = Queue[K]();
        var outU = u;
        
        // Build the path, with an extra clause to prevent infinite loops.
        while (previous.contains(outU) && !outPath.contains(outU)) {
          outPath = outU +: outPath;
          outU = previous(outU);
        }
        
        // If no cycle was found, keep the path.
        if (!outPath.contains(outU))
          outPaths.update(u, outPath);
        else
          println(s"Cycle detected while finding path from $startNode to $u at $outU.");
        
        endNodes -= u;
        if (endNodes.size <= 0)
          return outPaths.toMap;
      }
      
      for ((v, weight) <- connections(u)) {
        val alt = uDist + weight;
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
  def findPaths(endpointIds : Map[K, Iterable[K]]) : Map[(K, K), Queue[K]] = {
    val out = new TrieMap[(K, K), Queue[K]];
    
    val startTime = System.currentTimeMillis;
    
    val futures = new mutable.ArrayBuffer[Future[_]];
    for ((startId, endIds) <- endpointIds if endIds.nonEmpty) {
      val f : Future[Map[K, Queue[K]]] = Future {
        findPaths(startId, endIds);
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
