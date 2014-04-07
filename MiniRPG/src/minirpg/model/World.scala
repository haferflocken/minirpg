package minirpg.model

import scala.collection.mutable.ArraySeq

class World(tileGrid : TileGrid, iEntities : Array[Entity]) {
  def this(tileGrid : TileGrid) = this(tileGrid, Array[Entity]());
  
  var entities = ArraySeq(iEntities);

  def width = tileGrid.width;
  def height = tileGrid.height;
  def area = tileGrid.area;
  
}