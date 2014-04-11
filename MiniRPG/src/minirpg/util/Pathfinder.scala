package minirpg.util

import minirpg.model._
import scala.collection.immutable.Queue

object Pathfinder {

  def findPath(startX : Int, startY : Int, endX : Int, endY : Int, graph : Graph[_]) : Queue[(Int, Int)] = {
    return Queue((endX, endY)); // TODO Currently just returns the end point
  }
  
}