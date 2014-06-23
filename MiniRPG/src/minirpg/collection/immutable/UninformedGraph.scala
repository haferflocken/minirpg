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
 * Setting optimize to true will only work properly if all edges
 * have mutual edges in the other direction. Doing so when this is
 * not the case could result in pathfinding failures.
 */
class UninformedGraph[K](
  override val nodes : Set[K],
  override val connections : Map[K, Map[K, Int]],
  override val optimize : Boolean = false)
  extends AbstractGraph[K] {
  
  type This = UninformedGraph[K];
  
  
  /**
   * Find paths using Dijkstra's Algorithm, adapted from pseudocode courtesy of Wikipedia.
   */
  def findPaths(startId : K, endIds : Iterable[K]) : Map[K, Queue[K]] = {
    if (!nodes.contains(startId)) {
      println(s"No paths from $startId: node is not in graph.");
      return Map();
    }
    
    val endNodes = endIds.filter(e => e != null && nodes.contains(e)).toBuffer;
    if (optimize) {
      val subGraph = subGraphs.find(_.contains(startId)).get;
      val invalidEnds = endNodes.filterNot(subGraph contains _);
      if (invalidEnds.nonEmpty) {
    	endNodes --= invalidEnds;
        println(s"Optimization removed invalid nodes: $invalidEnds");
      }
    }
    if (endNodes.length == 0) {
      println(s"No paths from $startId: failed to get end nodes for " + endIds.mkString(", ") + ".");
      return Map();
    }
    
    val pq = new PQueue[K](nodes.size / 4);
    val dist = new mutable.HashMap[K, Int];
    val previous = new mutable.HashMap[K, K];
    val outPaths = new mutable.HashMap[K, Queue[K]];
    
    for (key <- nodes) dist(key) = Int.MaxValue;
    dist(startId) = 0;
    for ((node, distance) <- dist) pq += (node, distance);
    
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
          println(s"Cycle detected while finding path from $startId to $u at $outU.");
        
        endNodes -= u;
        if (endNodes.size <= 0)
          return outPaths.toMap;
      }
      
      for ((v, weight) <- connections(u)) {
        val alt = uDist + weight;
        if (alt < dist(v)) {
          dist(v) = alt;
          previous(v) = u;
          pq -= v;
          pq += (v, alt);
        }
      }
    }
    
    return outPaths.toMap;
  }
  
  
  def addConnections(es : (K, K, Int)*) : UninformedGraph[K] = {
    // TODO
    return null;
  };
  
  
  def removeConnections(es : (K, K)*) : UninformedGraph[K] = {
    // TODO
    return null;
  };
}
