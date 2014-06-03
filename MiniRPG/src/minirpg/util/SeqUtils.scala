package minirpg.util

object SeqUtils {
  
  def firstIntersection[A](seq1 : Seq[A], seq2 : Seq[A]) : Option[A] = {
    val intersections = seq1 intersect seq2;
    if (intersections.size == 0)
      return None;
    
    for (i <- 0 until seq1.length) {
      val e = seq1(i);
      if (intersections contains e)
        return Some(e);
    }
    
    return None;
  };

  def lastIntersection[A](seq1 : Seq[A], seq2 : Seq[A]) : Option[A] = {
    val intersections = seq1 intersect seq2;
    if (intersections.size == 0)
      return None;
    
    for (i <- Range(seq1.length - 1, -1, -1)) {
      val e = seq1(i);
      if (intersections contains e)
        return Some(e);
    }
    
    return None;
  };
}