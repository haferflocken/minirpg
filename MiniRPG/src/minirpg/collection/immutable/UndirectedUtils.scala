package minirpg.collection.immutable

import scala.collection.immutable.Queue
import scala.collection.mutable

object UndirectedUtils {
  
  class UndirectedGraph[K](graph : AbstractGraph[K]) {
    /**
     * Find the shortest paths from the several starts to the end.
     * Returns a map of startId -> path.
     */
    def findPaths[K](graph : AbstractGraph[K], startIds : Iterable[K], endId : K) : Map[K, Queue[K]] = {
      val reversedPaths = graph.findPaths(endId, startIds);
      return reversedPaths.map(t => (t._1, t._2.reverse));
    };
  }
  
  implicit def asUndirectedGraph[K](graph : AbstractGraph[K]) = new UndirectedGraph(graph);
  
  
  /**
   * Convert undirected edges to directed edges to use with the Graph classes.
   */
  def undirectedToDirected[K](connections : Iterable[(K, K, Int)]) : Map[K, Map[K, Int]] = {
    val buff = new mutable.HashMap[K, Map[K, Int]];
    
    for ((u, v, weight) <- connections) {
      if (buff contains u) {
        val cons = buff(u);
        buff(u) = cons + (v -> weight);
      }
      else
        buff(u) = Map(v -> weight);
      
      if (buff contains v) {
        val cons = buff(v);
        buff(v) = cons + (u -> weight);
      }
      else
        buff(v) = Map(u -> weight);
    }
    
    return buff.toMap;
  };

}