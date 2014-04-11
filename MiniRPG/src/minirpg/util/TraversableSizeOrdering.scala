package minirpg.util

import scala.math.Ordering
import scala.collection.Traversable

class TraversableSizeOrdering[T <: Traversable[_]] extends Ordering[T] {
  
  def compare(a : T, b : T) = a.size compare b.size;

}
