package minirpg.model

import scala.collection.mutable.ArraySeq
import scala.collection.immutable.Queue
import minirpg.util.Graph
import scalafx.scene.Node
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.util.Random

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
  private var _debugPathNodes : List[Node] = Nil;
  
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
  
  def getEntitiesAt(x : Int, y : Int) : Vector[Entity] = {
    return _entities.filter((e) => {e.x == x && e.y == y});
  }
  
  def getEntitiesById(id : String) : Vector[Entity] = {
    return _entities.filter(_.id == id);
  }
  
  def makeEntityId : String = Random.alphanumeric.mkString;
  
  def entites = _entities;
  def nodes = _nodes;
  def debugPathNodes = _debugPathNodes;

  val width = tileGrid.width;
  val height = tileGrid.height;
  val area = tileGrid.area;
  
  def findPath(x1 : Int, y1 : Int, x2 : Int, y2 : Int) : Queue[(Int, Int)] = {
    val startId = (x1, y1);
    val endId = (x2, y2);
    
    val path = Graph.findPath(startId, endId, tileGrid.navMap);
    if (path == null) {
      println("No path: failed to find path.");
      _debugPathNodes = Nil;
      return null;
    }
    val out = path.map(_.id);
    debugDisplayPath(out);
    return out;
  }
  
  def tick(delta : Long) : Unit = {
    for (e <- _entities) {
      e.tick(delta);
      updateEntityNodeCoords(e);
    }
  }
  
  def toJsonString = null;
  
  override def toString() = s"$name\n$tileGrid\nentities: " + _entities.mkString(", ");
  
  private def updateEntityNodeCoords(e : Entity) : Unit = {
    e.node.layoutX = e.x * tileGrid.tileWidth;
    e.node.layoutY = e.y * tileGrid.tileHeight;
  }
  
  private def debugDisplayPath(path : Queue[(Int, Int)]) = {
    if (minirpg.global_debugPaths) {
      _debugPathNodes = Nil;
      var i = 0;
      val length = path.length;
      for (point <- path) {
        _debugPathNodes = new Circle() {
          centerX = point._1 * tileGrid.tileWidth + tileGrid.tileWidth / 2;
          centerY = point._2 * tileGrid.tileHeight + tileGrid.tileHeight / 2;
          radius = (tileGrid.tileWidth / 4) min (tileGrid.tileHeight / 4);
          fill = Color.rgb(255 * (length - i - 1) / length, 255 * (i + 1) / length, 0);
        } :: _debugPathNodes;
        i = i + 1;
      }
    }
  }
}