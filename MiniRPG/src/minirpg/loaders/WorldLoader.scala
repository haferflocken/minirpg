package minirpg.loaders

import minirpg.model._
import java.io.InputStream
import scala.collection.Map
import scala.util.parsing.json.JSONObject
import scala.util.parsing.json.JSONArray
import minirpg.model.world.Entity
import minirpg.model.world.World
import java.io.File

object WorldLoader extends Loader[World] {
  
  val dirPath = "res/worlds";
  val centerBarrowsDirPath = s"$dirPath/centerBarrows";
  val outerBarrowsDirPath = s"$dirPath/outerBarrows";
  val nonBarrowsDirPath = s"$dirPath/nonBarrows";
  
  
  val centerBarrowPaths = new File(centerBarrowsDirPath).list.toVector.map(centerBarrowsDirPath + "/" + _);
  val outerBarrowPaths = new File(outerBarrowsDirPath).list.toVector.map(outerBarrowsDirPath + "/" + _);
  val nonBarrowPaths = new File(nonBarrowsDirPath).list.toVector.map(nonBarrowsDirPath + "/" + _);
  val barrowPaths = centerBarrowPaths ++: outerBarrowPaths;
  val allPaths = barrowPaths ++: nonBarrowPaths;
  
  
  def randomCenterBarrowPath = centerBarrowPaths(Math.random * centerBarrowPaths.length toInt);
  
  
  def nOuterBarrowPaths(n : Int) : Vector[String] = {
    val pool = outerBarrowPaths.toBuffer;
    while (pool.length < n) {
      pool ++= nonBarrowPaths;
    }
    
    var out = Vector[String]();
    for (k <- 0 until n) {
      val i = (Math.random * pool.length) toInt;
      out = pool(i) +: out;
      pool.remove(i);
    }
    return out;
  }
  
  
  def nNonBarrowPaths(n : Int) : Vector[String] = {
    val pool = nonBarrowPaths.toBuffer;
    while (pool.length < n) {
      pool ++= nonBarrowPaths;
    }
    
    var out = Vector[String]();
    for (k <- 0 until n) {
      val i = (Math.random * pool.length) toInt;
      out = pool(i) +: out;
      pool.remove(i);
    }
    return out;
  }

  
  def loadJsonString(filePath : String, data : String) : World = {
    val jsonMap = parseRaw(filePath, data);
    if (jsonMap == null)
      return null;
    
    val jsonName = jsonMap.getOrElse("name", null);
    if (jsonName == null || !jsonName.isInstanceOf[String]) {
      warnInvalidField("name", filePath);
      return null;
    }
    val jsonTileGrid = jsonMap.getOrElse("tileGrid", null);
    if (jsonTileGrid == null || !jsonTileGrid.isInstanceOf[String]) {
      warnInvalidField("tileGrid", filePath);
      return null;
    }
    val jsonEntities = jsonMap.getOrElse("entities", null);
    if (jsonEntities == null || !jsonEntities.isInstanceOf[JSONArray]) {
      warnInvalidField("entities", filePath);
      return null;
    }
    
    val name = jsonName.asInstanceOf[String];
    val tileGridFilePath = jsonTileGrid.asInstanceOf[String];
    val entities = makeEntities(filePath, jsonEntities.asInstanceOf[JSONArray].list);
    if (entities == null)
      return null;
    
    val tileGrid = TileGridLoader.loadJsonFile(tileGridFilePath);
    if (tileGrid == null)
      return null;
    
    return new World(name, tileGrid, entities);
  }
  
  
  private def makeEntities(filePath : String, raw : List[Any]) : Vector[Entity] = {
    val objs = for (o <- raw if o.isInstanceOf[JSONObject]) yield o.asInstanceOf[JSONObject].obj;
    val entities = for (m <- objs if m != null; e = makeEntity(filePath, m) if e != null) yield e;
    return entities.toVector;
  }
  
  
  private def makeEntity(filePath : String, raw : Map[String, Any]) : Entity = {
    val rawId = raw.getOrElse("id", null);
    if (rawId == null || !rawId.isInstanceOf[String]) {
      warnInvalidField("id", filePath);
      return null;
    }
    val id = rawId.asInstanceOf[String];
    
    val rawClass = raw.getOrElse("class", null);
    if (rawClass == null || !rawClass.isInstanceOf[String]) {
      warnInvalidField("class", filePath);
      return null;
    }
    val className = rawClass.asInstanceOf[String];
    
    val buildClass = Builder.classFrom(className);
    if (buildClass == null || !classOf[Entity].isAssignableFrom(buildClass)) {
      warnInvalidField("class", filePath);
      println("EXTRA: " + className + " is not an Entity.");
      return null;
    }
    
    val rawArgs = raw.getOrElse("args", null);
    if (rawArgs == null || !rawArgs.isInstanceOf[JSONObject]) {
      warnInvalidField("args", filePath);
      return null;
    }
    val args = rawArgs.asInstanceOf[JSONObject].obj;
    if (args == null) {
      warnInvalidField("args", filePath);
      println("EXTRA: obj field of JSONObject is null.");
      return null;
    }
    
    val o = Builder.build(className, id, args);
    if (o == null) 
      return null;
    
    return o.asInstanceOf[Entity];
  }
}