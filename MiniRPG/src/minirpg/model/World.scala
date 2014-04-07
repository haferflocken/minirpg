package minirpg.model

import scala.collection.mutable.ArraySeq

class World(
    val tileGrid : TileGrid,
    private var _entities : Vector[Entity]) {
  
  def this(tileGrid : TileGrid) = this(tileGrid, Vector[Entity]());
  
  private var _entityNodes = _entities.map(_.node).filter(_ != null);
  
  def addEntity(e : Entity) : Unit = {
    _entities = _entities :+ e;
    if (e.node != null)
      _entityNodes = _entityNodes :+ e.node;
  }
  
  def removeEntity(e : Entity) : Unit = {
    _entities = _entities.filter(_ != e);
    if (e.node != null)
      _entityNodes = _entityNodes.filter(_ != e.node);
  }
  
  def entites = _entities;
  def entityNodes = _entityNodes;

  def width = tileGrid.width;
  def height = tileGrid.height;
  def area = tileGrid.area;
  
}