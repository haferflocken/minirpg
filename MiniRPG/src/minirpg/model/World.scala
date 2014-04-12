package minirpg.model

import scala.collection.mutable.ArraySeq
import scala.collection.immutable.Queue
import minirpg.util.Graph

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
  
  def getEntityById(id : String) : Entity = {
    val potential = _entities.filter(_.id == id);
    if (potential.nonEmpty)
      return potential(0);
    return null;
  }
  
  def entites = _entities;
  def nodes = _nodes;

  val width = tileGrid.width;
  val height = tileGrid.height;
  val area = tileGrid.area;
  
  def findPath(x1 : Int, y1 : Int, x2 : Int, y2 : Int) : Queue[(Int, Int)] = {
    val startId = (x1, y1);
    val endId = (x2, y2);
    
    val path = Graph.findPath(startId, endId, tileGrid.navMap);
    if (path == null) {
      println("No path: failed to find path.");
      return null;
    }
    return path.map(_.id);
  }
  
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