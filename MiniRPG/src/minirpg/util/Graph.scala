package minirpg.util

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ArrayStack

class Graph[E](
    val id : String,
    var data : E = null,
    val connections : ArrayBuffer[Graph[E]] = new ArrayBuffer[Graph[E]],
    val weights : ArrayBuffer[Int] = new ArrayBuffer[Int]) {

  def connectTo(g : Graph[E], weight : Int) : Unit = {
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
  
  def foreach(f : (Graph[E]) => Unit) : Unit = {
    val stack : ArrayStack[Graph[E]] = new ArrayStack();
    stack.push(this);
    
    while (stack.nonEmpty) {
      val g = stack.pop();
      f(g);
      for (x <- g.connections)
        stack.push(x);
    }
  }
  
  override def equals(o : Any) : Boolean = {
    if (!o.isInstanceOf[Graph[E]])
      return false;
    return hashCode == o.hashCode;
  }
  
  override def hashCode : Int = id.hashCode + data.hashCode * 3 + connections.hashCode * 5 + weights.hashCode * 7;
  
}

object Graph {
  
  def apply[E >: AnyRef](ids : Array[String], rawCons : Map[String, Array[(String, Int)]]) : Map[String, Graph[E]] = 
    apply(ids.map((_, null)).toMap, rawCons);
  
  def apply[E](rawData : Map[String, E], rawCons : Map[String, Array[(String, Int)]]) : Map[String, Graph[E]] = {
    val gMap = rawData.map((entry : (String, E)) => (entry._1, new Graph[E](entry._1, entry._2)));
    
    for (e <- rawCons) {
      val g = e._1;
      for (c <- e._2) {
        gMap(g).connectTo(gMap(c._1), c._2);
      }
    }
    
    return gMap;
  }
}