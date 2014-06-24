package minirpg.collection.immutable

import scala.collection.mutable;
import scala.collection.immutable.Queue
import minirpg.collection.mutable.PQueue

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
    
    val open = new PQueue[K](nodes.size / 4); // The open set ordered by estimated cost.
    val closed = new mutable.HashSet[K]; // The nodes that have been evaluated already.
    val dist = new mutable.HashMap[K, Int]; // The actual cost so far to a node.
    val estimate = new mutable.HashMap[(K, K), Int]; // The estimated cost to a node to a goal.
    val previous = new mutable.HashMap[K, K];
    val outPaths = new mutable.HashMap[K, Queue[K]];
    
    open += (startId, 0);
    dist(startId) = 0;
    for (goal <- endNodes)
      estimate((startId, goal)) = dist(startId) + heuristic(startId, goal);
    
    while (open.nonEmpty) {
      val (u, uDist) = open.dequeue;
      
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
      
      closed += u;
      for ((v, weight) <- connections(u) if (!closed.contains(v))) {
        val alt = uDist + weight;
        if (!open.contains(v) || alt < dist(v)) {
          dist(v) = alt;
          previous(v) = u;
          var minEstimate = Int.MaxValue;
          for (goal <- endNodes) {
            val est = alt + heuristic(v, goal);
            if (est < minEstimate) 
              minEstimate = est;
            estimate((v, goal)) = est;
          }
          if (!open.contains(v))
            open += (v, minEstimate);
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
    
    return new HeuristicGraph(nodes, outConnections, heuristic, false);
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