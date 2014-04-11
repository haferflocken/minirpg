package minirpg.model

import java.io.InputStream
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONObject

trait Loader[E] {

  def loadJsonFile(filePath : String) : E = {
    val source = io.Source.fromFile(filePath);
    val lines = source.mkString;
    source.close();
    
    println("Loading \"" + filePath + "\" produced:\n" + lines);
    
    return loadJsonString(filePath, lines);
  }
  
  def loadJsonString(filePath : String, data : String) : E;
  
  protected def parseRaw(filePath : String, data : String) : Map[String, Any] = {
    val jsonMaybe = JSON.parseRaw(data);
    if (jsonMaybe.isEmpty) {
      println("\"" + filePath + "\" is not valid JSON.");
      return null;
    }
    val json = jsonMaybe.get;
    if (!json.isInstanceOf[JSONObject]) {
      println("The first entry of \"" + filePath + "\" is not a JSONObject.")
      return null;
    }
    
    return json.asInstanceOf[JSONObject].obj;
  }
    
  protected def warnInvalidField(fieldName : String, filePath : String) =
    println("Field \"" + fieldName + "\" is missing from or improperly defined in \"" + filePath + "\".");
  
}