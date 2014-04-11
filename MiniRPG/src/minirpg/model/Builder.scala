package minirpg.model

import scala.collection.mutable.HashMap

trait Builder[E] {

  def build(args : Map[String, Any]) : E;
  val buildName : String;
  val buildClass : Class[E];
  
}

final object Builder {
  
  private val builders : HashMap[String, Builder[_]] = new HashMap[String, Builder[_]]();
  
  def register(b : Builder[_]) : Unit = builders.put(b.buildName, b);
  
  def build(name : String, args : Map[String, Any]) : Any = {
    val bldr = builders.getOrElse(name, null);
    if (bldr == null)
      return null;
    return bldr.build(args);
  }
  
  def classFrom(name : String) : Class[_] = {
    val bldr = builders.getOrElse(name, null);
    if (bldr == null) 
      return null;
    return bldr.buildClass;
  }
  
  register(minirpg.actors.HumanBuilder)
  
}