package minirpg.model

import minirpg.builderMap
import scala.collection.mutable.HashMap
import minirpg.entities.actors.HumanBuilder
import scala.reflect.runtime.universe._

trait Builder[E] {

  def build(id : String, args : Map[String, Any]) : E;
  val buildName : String;
  val buildClass : Class[_];
  
  def extract[T](varName : String, args : Map[String, Any], fail : T) : T = {
    val rawVar = args.getOrElse(varName, fail);
    if (rawVar == null || rawVar == fail) {
      println("Failed to find argument \"" + varName + "\" of class \"" + buildName + "\".");
      return fail;
    }
    try {
      val outVar = rawVar.asInstanceOf[T];
      return outVar;
    }
    catch {
      case e : ClassCastException =>
        println("Argument \"" + varName + "\" of class \"" + buildName + "\" has the incorrect type.");
    }
    return fail;
  }

}

final object Builder {
  
  def build(name : String, id : String, args : Map[String, Any]) : Any = {
    val bldr = builderMap.getOrElse(name, null);
    if (bldr == null)
      return null;
    return bldr.build(id, args);
  }
  
  def classFrom(name : String) : Class[_] = {
    val bldr = builderMap.getOrElse(name, null);
    if (bldr == null) 
      return null;
    return bldr.buildClass;
  }
  
}