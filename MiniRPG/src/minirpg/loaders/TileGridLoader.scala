package minirpg.loaders

import minirpg.model._
import java.io.InputStream
import scalafx.scene.image.Image
import scala.util.parsing.json.JSONArray
import scala.util.parsing.json.JSONObject
import minirpg.model.world.TileGrid

object TileGridLoader extends Loader[TileGrid] {

  private type stringType = String;
  private type anyType = Any;
  
  def loadJsonString(filePath : String, data : String) : TileGrid = {
    val jsonMap = parseRaw(filePath, data);
    if (jsonMap == null)
      return null;
    
    val jsonGrid = jsonMap.getOrElse("grid", null);
    if (jsonGrid == null || !jsonGrid.isInstanceOf[JSONArray]) {
      warnInvalidField("grid", filePath);
      return null;
    }
    val jsonTileMap = jsonMap.getOrElse("tileMap", null);
    if (jsonTileMap == null || !jsonTileMap.isInstanceOf[JSONObject]) {
      warnInvalidField("tileMap", filePath);
      return null;
    }
    val jsonTileWidth = jsonMap.getOrElse("tileWidth", null);
    if (jsonTileWidth == null || !jsonTileWidth.isInstanceOf[Double]) {
      warnInvalidField("tileWidth", filePath);
      return null;
    }
    val jsonTileHeight = jsonMap.getOrElse("tileHeight", null);
    if (jsonTileHeight == null || !jsonTileHeight.isInstanceOf[Double]) {
      warnInvalidField("tileHeight", filePath);
      return null;
    }
    
    val grid = makeGrid(filePath, jsonGrid.asInstanceOf[JSONArray].list);
    if (grid == null)
      return null;
    val tileMap = makeTileMap(filePath, jsonTileMap.asInstanceOf[JSONObject].obj);
    if (tileMap == null)
      return null;
    val tileWidth = jsonTileWidth.asInstanceOf[Double].intValue;
    val tileHeight = jsonTileHeight.asInstanceOf[Double].intValue;
    
    return new TileGrid(grid, tileMap, tileWidth, tileHeight);
  }
  
  private def makeGrid(filePath : String, raw : List[Any]) : Array[Array[Int]] = {
    var buff = List[Array[Int]]();
    raw.foreach((rawCol : Any) => {
      if (!rawCol.isInstanceOf[JSONArray]) {
        println("Field \"grid\" must be an array of arrays. This is not the case in \"" + filePath + "\".");
        return null;
      }
      val col = makeCol(filePath, rawCol.asInstanceOf[JSONArray].list);
      if (col == null)
        return null;
      buff = col +: buff;
    });
    return buff.toArray;
  }
  
  private def makeCol(filePath : String, raw : List[Any]) : Array[Int] = {
    var buff = List[Int]();
    raw.foreach((rawVal : Any) => {
      if (!rawVal.isInstanceOf[Double]) {
        println("Entries in the arrays of field \"grid\" must be numbers. This is not the case in \"" + filePath + "\".");
        return null;
      }
      buff = rawVal.asInstanceOf[Double].intValue +: buff;
    });
    return buff.reverse.toArray;
  }
  
  private def makeTileMap(filePath : String, raw : Map[String, Any]) : Map[Int, Image] = {
    try {
      return raw.map((e : (String, Any)) =>
        (e._1.toInt,
            if (e._2 == null)
              null
            else
              new Image("file:" + e._2.asInstanceOf[String])));
    }
    catch {
      case e : NumberFormatException => print("Keys in field \"tileMap\" must be integers");
      case e : ClassCastException => print("Values in field \"tileMap\" must be strings");
      case e : IllegalArgumentException => print("Values in field \"tileMap\" must be valid file paths");
      println(", which is not the case in \"" + filePath + "\". From: ");
      println(e);
    }
    return null;
  }

}