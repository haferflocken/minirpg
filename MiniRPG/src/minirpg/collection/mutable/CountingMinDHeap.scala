package minirpg.collection.mutable

class CountingMinDHeap[E](override val d : Int, initialCapacity : Int = 16) extends MinDHeap[E](d, initialCapacity) {
  
  val counter = new Counter[E];
  
  override def add(e : E, p : Int) : Unit = {
    super.add(e, p);
    counter.add(e);
  };
  
  override def deleteMin : (E, Int) = {
    val out = super.deleteMin;
    counter.remove(out._1);
    return out;
  };
  
  override def contains(e : E) = counter.count(e) > 0;

}