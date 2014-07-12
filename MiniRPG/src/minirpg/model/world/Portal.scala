package minirpg.model.world

class Portal(val gridX : Int, val gridY : Int, val overworldX : Int, val overworldY : Int) {

  private val hasher = (gridX, gridY, overworldX, overworldY);
  
  override def hashCode = hasher.hashCode;
  
  override def equals(o : Any) = o.hashCode == hashCode;
  
}