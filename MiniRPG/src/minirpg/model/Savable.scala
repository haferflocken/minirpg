package minirpg.model

trait Savable {
  
  def toByteArray() : Traversable[Byte];

}