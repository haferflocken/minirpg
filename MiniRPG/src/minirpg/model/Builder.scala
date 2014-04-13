package minirpg.model

import scala.collection.mutable.HashMap
import minirpg.entities.actors.HumanBuilder

trait Builder[E] {

  def build(id : String, args : Map[String, Any]) : E;
  val buildName : String;
  val buildClass : Class[E];
  
}

final object Builder {
  
  private val builders =
    Vector[Builder[_]](
      GearEntityBuilder,
      HumanBuilder
    ).map((e) => (e.buildName, e)).toMap[String, Builder[_]];
  
  def build(name : String, id : String, args : Map[String, Any]) : Any = {
    val bldr = builders.getOrElse(name, null);
    if (bldr == null)
      return null;
    return bldr.build(id, args);
  }
  
  def classFrom(name : String) : Class[_] = {
    val bldr = builders.getOrElse(name, null);
    if (bldr == null) 
      return null;
    return bldr.buildClass;
  }
  
}