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
abstract class AbstractGraph[K] {
  
  val nodes : Set[K];
  val connections : Map[K, Map[K, Int]];
  val optimize : Boolean;
  type This <: AbstractGraph[K];
  
  
  /**
   * Figure out the sub-graphs of this graph in order to speed up pathfinding.
   * Is null if optimize == false.
   */
  val subGraphs : Vector[Set[K]] = {
    if (!optimize) {
      null;
    }
    else {
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
          
          //println("Found a sub-graph of size " + visited.size + ".");
          out = out :+ visited.toSet;
        }
      }
      
      //println("Found " + out.length + " sub-graphs.");
      out;
    }
  };
  
  
  /**
   * Render this graph using a function to render nodes and a function to render edges.
   */
  def render(rNode : (K) => Unit, rEdge : (K, K, Int) => Unit) : Unit = {
    for ((a, edges) <- connections; (b, weight) <- edges) 
      rEdge(a, b, weight);
    for (n <- nodes)
      rNode(n);
  };
  
  
  /**
   * Find the shortest path through a graph.
   */
  def findPath(startId : K, endId : K) : Queue[K] = {
    val paths = findPaths(startId, Vector(endId));
    if (paths.nonEmpty)
      return paths(endId);
    return null;
  }
  
  
  /**
   * Find the shortest paths from the start to the ends.
   * Subclasses should override this with an appropriate algorithm.
   * Returns a map of endId -> path.
   */
  def findPaths(startId : K, endIds : Iterable[K]) : Map[K, Queue[K]];
  
  
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
  
  
  def addConnections(es : (K, K, Int)*) : This;
  
  def removeConnections(es : (K, K)*) : This;
  
  def setConnections(o : Map[K, Map[K, Int]]) : This;
  
}
