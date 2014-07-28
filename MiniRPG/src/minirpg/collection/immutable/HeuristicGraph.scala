package minirpg.collection.immutable

import scala.collection.mutable
import scala.collection.immutable.Queue
import minirpg.collection.mutable.CountingMinDHeap

class HeuristicGraph[K](
    override val nodes : Set[K],
    override val connections : Map[K, Map[K, Int]],
    val heuristic : (K, K) => Int,
    override val optimize : Boolean = false)
    extends AbstractGraph[K] {
  
  type This = HeuristicGraph[K];
  
  
  /**
   * Find paths using A*.
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
    
    val d = connections.foldLeft(0)((b, x) => b + x._2.size) / nodes.size; // Calculate d for the heap (edges / nodes).
    val open = new CountingMinDHeap[K](d, nodes.size / 4); // The open set ordered by estimated cost.
    val closed = new mutable.HashSet[K]; // The nodes that have been evaluated already.
    val dist = new mutable.HashMap[K, Int]; // The actual cost so far to a node.
    val previous = new mutable.HashMap[K, K];
    val outPaths = new mutable.HashMap[K, Queue[K]];
    
    open.add(startId, 0);
    dist(startId) = 0;
    
    while (open.nonEmpty) {
      val (u, uDist) = open.deleteMin;
      
      // If we have found a path we are looking for, save it.
      if (endNodes.contains(u)) {
        // Build the path.
        var outPath = Queue[K]();
        var outU = u;
        while (previous.contains(outU)) {
          outPath = outU +: outPath;
          outU = previous(outU);
        }
        
        // Save the path and stop looking for it.
        outPaths.update(u, outPath);
        endNodes -= u;
        if (endNodes.size <= 0)
          return outPaths.toMap;
      }
      
      // Add the node to the closed set, then go over its unvisited neighbors.
      closed += u;
      for ((v, weight) <- connections(u) if !closed.contains(v)) {
        val alt = uDist + weight;
        
        // If we haven't considered this node or if we found a shorter path to it,
        // re-estimate its cost and then add it to the open set or update its priority.
        if (!open.contains(v) || alt < dist(v)) {
          dist(v) = alt;
          previous(v) = u;
          var minEstimate = Int.MaxValue;
          for (goal <- endNodes) {
            val est = alt + heuristic(v, goal);
            if (est < minEstimate) 
              minEstimate = est;
          }
          if (!open.contains(v))
            open.add(v, minEstimate);
          else {
            open.updatePriority(v, minEstimate);
          }
        }
      }
    }
    
    return outPaths.toMap;
  }
  
  
  def addConnections(es : (K, K, Int)*) : HeuristicGraph[K] = {
    var outConnections = connections;
    for ((a, b, weight) <- es) {
      val cons = connections.getOrElse(a, Map[K, Int]());
      val outCons = cons + ((b, weight));
      outConnections = (outConnections - a) + ((a, outCons));
    }
    
    return new HeuristicGraph(nodes, outConnections, heuristic, false);
  };
  
  
  def removeConnections(es : (K, K)*) : HeuristicGraph[K] = {
    var outConnections = connections;
    for ((a, b) <- es) {
      val cons = connections.getOrElse(a, null);
      if (cons != null && cons.contains(b)) {
        val outCons = cons - b;
        outConnections = (outConnections - a) + ((a, outCons));
      }
    }
    
    if (outConnections != connections)
      return new HeuristicGraph(nodes, outConnections, heuristic, false);
    return this;
  };
  
  
  def setConnections(o : Map[K, Map[K, Int]]) : HeuristicGraph[K] = 
    new HeuristicGraph(nodes, o, heuristic, optimize);

}

object HeuristicGraph {
  
  def manhattanDist(c1 : (Int, Int), c2 : (Int, Int)) : Int =
    Math.abs(c1._1 - c2._1) + Math.abs(c1._2 - c2._2);
  
  def lineDist(c1 : (Int, Int), c2 : (Int, Int)) : Int =
    (Math.sqrt(Math.pow(c1._1 - c2._1, 2) + Math.pow(c1._2 - c2._2, 2))).toInt;
  
}