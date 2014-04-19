package minirpg.model

import scala.collection.mutable.ArraySeq
import scala.collection.immutable.Queue
import minirpg.util.Graph
import scalafx.Includes.handle 
import scalafx.scene.Node
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.util.Random
import scalafx.scene.layout.Pane
import scalafx.animation.Animation
import scalafx.animation.TranslateTransition
import scalafx.util.Duration

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
  val canvas = new Pane {
    children.add(tileGrid.node);
    _entities.map(_.node).filter(_ != null).foreach((n) => {
      children.add(n);
    });
  };
  val particleCanvas = new ParticlePane;
  val debugCanvas = new Pane;
  
  def addEntity(e : Entity) : Unit = {
    _entities = _entities :+ e;
    e.world = this;
    updateEntityNodeCoords(e);
    
    if (e.node != null) 
      canvas.children.add(e.node);
  }
  
  def removeEntity(e : Entity) : Unit = {
    _entities = _entities.filter(_ != e);
    e.world = null;
    if (e.node != null)
      canvas.children.remove(e.node);
  }
  
  def getEntitiesAt(x : Int, y : Int) : Vector[Entity] = {
    return _entities.filter((e) => {e.x == x && e.y == y});
  }
  
  def getEntitiesIn(region : Region) : Vector[Entity] = {
    return _entities.filter(e => region.contains(e.x, e.y));
  }
  
  def getEntitiesById(id : String) : Vector[Entity] = {
    return _entities.filter(_.id == id);
  }
  
  def makeEntityId : String = Random.nextLong.toString;
  
  def entites = _entities;

  val width = tileGrid.width;
  val height = tileGrid.height;
  val area = tileGrid.area;
  
  def findPath(x1 : Int, y1 : Int, x2 : Int, y2 : Int) : Queue[(Int, Int)] = {
    val startId = (x1, y1);
    val endId = (x2, y2);
    
    val path = Graph.findPath(startId, endId, tileGrid.navMap);
    if (path == null) {
      println("No path: failed to find path.");
      debugCanvas.children.clear;
      return null;
    }
    val out = path.map(_.id);
    debugDisplayPath(out);
    return out;
  }
  
  def getSpotNextTo(x : Int, y : Int) : (Int, Int) = {
    val connections = tileGrid.getConnections(x, y);
    if (connections.isEmpty)
      return (x, y);
    for ((p, c) <- connections) {
      if (getEntitiesAt(p._1, p._2).isEmpty) return p;
    }
    return (x, y);
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
    if (e.node != null) {
      e.node.layoutX = e.x * tileGrid.tileWidth;
      e.node.layoutY = e.y * tileGrid.tileHeight;
    }
  }
  
  private def debugDisplayPath(path : Queue[(Int, Int)]) = {
    if (minirpg.global_debugPaths) {
      debugCanvas.children.clear;
      var i = 0;
      val length = path.length;
      for (point <- path) {
        val c = new Circle() {
          centerX = point._1 * tileGrid.tileWidth + tileGrid.tileWidth / 2;
          centerY = point._2 * tileGrid.tileHeight + tileGrid.tileHeight / 2;
          radius = (tileGrid.tileWidth / 4) min (tileGrid.tileHeight / 4);
          fill = Color.rgb(255 * (length - i - 1) / length, 255 * (i + 1) / length, 0);
        };
        debugCanvas.children.add(c);
        i = i + 1;
      }
    }
  }
}

class ParticlePane extends Pane {
  
  def components(speed : Double, direction : Double) =
    (speed * Math.cos(direction), speed * Math.sin(direction));
  
  def randVelocity(minSpeed : Double, maxSpeed : Double, minAngle : Double = 0.0, maxAngle : Double = Math.PI * 2.0) = {
    val speed = (maxSpeed - minSpeed) * Math.random + minSpeed;
    val direction = (maxAngle - minAngle) * Math.random + minAngle;
    components(speed, direction);
  }
  
  def mkPoint(x : Double, y : Double, velocity : (Double, Double)) : Unit =
    mkPoint(x, y, velocity._1, velocity._2);

  def mkPoint(x : Double, y : Double, xSpeed : Double, ySpeed : Double) : Unit = {
    val particle = new Circle {
      radius = 2;
      centerX = x;
      centerY = y;
      fill = Color.AQUA;
    }
    val duration = new Duration(Duration(500));
    val translate = new TranslateTransition(duration, particle) {
      byX = xSpeed; 
      byY = ySpeed;
      onFinished = handle { children.remove(particle) };
    };
    children.add(particle);
    translate.play;
  }
  
}
