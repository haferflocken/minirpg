package minirpg.model

import java.io.InputStream

trait Loader[E] {

  def load(stream : InputStream) : E;
  
}