package minirpg.model

import java.io.InputStream

trait Loader[E] {

  def loadJsonString(data : String) : E;
  
}