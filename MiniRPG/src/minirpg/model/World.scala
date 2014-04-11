package minirpg.model

import scala.collection.mutable.ArraySeq

class World(
    val name : String,
    val tileGrid : TileGrid,
    private var _entities : Vector[Entity])
    extends Savable {
  
  def this(name : String, tileGrid : TileGrid) = this(name, tileGrid, Vector[Entity]());
  
  _entities.foreach((e : Entity) => {
    e.world = this;
    updateEntityNodeCoords(e)
  });
  private var _nodes = tileGrid.node +: _entities.map(_.node).filter(_ != null);
  
  def addEntity(e : Entity) : Unit = {
    _entities = _entities :+ e;
    e.world = this;
    updateEntityNodeCoords(e);
    
    if (e.node != null) 
      _nodes = _nodes :+ e.node;
  }
  
  def removeEntity(e : Entity) : Unit = {
    _entities = _entities.filter(_ != e);
    e.world = null;
    if (e.node != null)
      _nodes = _nodes.filter(_ != e.node);
  }
  
  def entites = _entities;
  def nodes = _nodes;

  val width = tileGrid.width;
  val height = tileGrid.height;
  val area = tileGrid.area;
  
  def update : Unit = {
    _entities.foreach(updateEntityNodeCoords(_));
  }
  
  def toJsonString = null;
  
  override def toString() = s"$name\n$tileGrid\nentities: " + _entities.mkString(", ");
  
  private def updateEntityNodeCoords(e : Entity) : Unit = {
    e.node.layoutX = e.x * tileGrid.tileWidth;
    e.node.layoutY = e.y * tileGrid.tileHeight;
  }
  
}