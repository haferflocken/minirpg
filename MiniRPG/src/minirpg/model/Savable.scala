package minirpg.model

trait Savable {
  
  def toJsonString() : String;

}