package minirpg.collection.mutable

import scala.collection.mutable
import scala.collection.immutable.Queue
import scala.collection.concurrent.TrieMap
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

/**
 * An immutable graph.
 */
class Graph[K](val nodes : Set[K], val connections : Map[K, Iterable[(K, Int)]]) {
  
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
