package minirpg.model

class World(tileGrid : TileGrid, iEntities : Array[Entity]) {
  def this(tileGrid : TileGrid) = this(tileGrid, Array[Entity]());
  
  var entities = iEntities;
  
}