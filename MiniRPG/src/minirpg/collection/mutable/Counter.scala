package minirpg.collection.mutable

import collection.mutable

class Counter[E] {
  
  val map = new mutable.HashMap[E, Int];
  
  def add(e : E) : Unit = {
    val oldCount = count(e);
    map.update(e, oldCount + 1);
  };
  
  def remove(e : E) : Unit = {
    val oldCount = count(e);
    if (oldCount == 0)
      return;
    if (oldCount == 1)
      map.remove(e);
    else
      map.update(e, oldCount - 1);
  };

  def count(e : E) : Int = map.getOrElse(e, 0);
  
}