package minirpg.model.world

import scalafx.Includes.handle
import scalafx.animation.TranslateTransition
import scalafx.scene.shape.Circle
import scalafx.scene.shape.Circle.sfxCircle2jfx
import scalafx.scene.paint.Color
import scalafx.scene.paint.Paint
import scalafx.scene.layout.Pane
import scalafx.scene.image.ImageView.sfxImageView2jfx
import scalafx.scene.Node.sfxNode2jfx
import scalafx.util.Duration
import minirpg.model._
import minirpg.model.world._
import minirpg.collection.immutable.Graph
import scala.collection.immutable.Queue
import scala.util.Random
import java.io.File

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
  lazy val canvas = new Pane {
    children.add(tileGrid.node);
    _entities.map(_.node).filter(_ != null).foreach((n) => {
      children.add(n);
    });
  };
  lazy val particleCanvas = new ParticlePane;
  lazy val debugCanvas = new Pane;
  
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
    
    val path = tileGrid.navMap.findPath(startId, endId);
    if (path == null) {
      println("No path: failed to find path.");
      debugCanvas.children.clear;
      return null;
    }
    debugDisplayPath(path);
    return path;
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

object World {
  
  val dirPath = "res/worlds";
  
  val centerBarrowPath = s"$dirPath/centerBarrow.json";
  val outerBarrowPaths = Vector(
    s"$dirPath/alligatorBarrow.json",
    s"$dirPath/valorBarrow.json",
    s"$dirPath/wraithBarrow.json",
    s"$dirPath/slimBarrow.json",
    s"$dirPath/mommaBarrow.json",
    s"$dirPath/poppaBarrow.json",
    s"$dirPath/carpenterBarrow.json",
    s"$dirPath/dandelionBarrow.json");
  val barrowPaths = centerBarrowPath +: outerBarrowPaths;
  
  val filePaths : Vector[String] = {
    val rootPath = new File("").getAbsolutePath;
    val worldFiles = new File(dirPath).listFiles.toVector;
    worldFiles.map(f => f.getAbsolutePath.substring(rootPath.length + 1).replace('\\', '/'));
  }
  
  val nonBarrowPaths = filePaths.filter(fP => barrowPaths.forall(bP => !fP.endsWith(bP) && !bP.endsWith(fP)));
  
  def nOuterBarrowPaths(n : Int) : Vector[String] = {
    val pool = outerBarrowPaths.toBuffer;
    while (pool.length < n) {
      pool ++= nonBarrowPaths;
    }
    
    var out = Vector[String]();
    for (k <- 0 until n) {
      val i = (Math.random * pool.length) toInt;
      out = pool(i) +: out;
      pool.remove(i);
    }
    return out;
  }
  
  def nNonBarrowPaths(n : Int) : Vector[String] = {
    val pool = nonBarrowPaths.toBuffer;
    while (pool.length < n) {
      pool ++= nonBarrowPaths;
    }
    
    var out = Vector[String]();
    for (k <- 0 until n) {
      val i = (Math.random * pool.length) toInt;
      out = pool(i) +: out;
      pool.remove(i);
    }
    return out;
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
  
  def randVelocities(minSpeed : Double, maxSpeed : Double, minAngle : Double = 0.0, maxAngle : Double = Math.PI * 2.0, num : Int) = 
    for (i <- 0 until num) yield randVelocity(minSpeed, maxSpeed, minAngle, maxAngle);

  def mkCircle(x : Double, y : Double, r : Double, f : Paint, xSpeed : Double, ySpeed : Double) : Unit = {
    val particle = new Circle {
      radius = r;
      centerX = x;
      centerY = y;
      fill = f;
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
  
  def mkCircles(x : Double, y : Double, r : Double, f : Paint, speeds : IndexedSeq[(Double, Double)]) : Unit = {
    for (i <- 0 until speeds.length) {
      mkCircle(x, y, r, f, speeds(i)._1, speeds(i)._2);
    }
  }
  
}
