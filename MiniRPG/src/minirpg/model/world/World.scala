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
import scala.collection.mutable
import scala.collection.immutable.Queue
import scala.util.Random
import java.io.File
import scalafx.scene.Node
import scalafx.animation.Animation
import scalafx.animation.PauseTransition
import scalafx.animation.ParallelTransition
import scalafx.scene.Group
import scalafx.collections.ObservableBuffer
import scalafx.animation.FadeTransition

class World(
    val name : String,
    val tileGrid : TileGrid)
    extends Savable {
  
  private val _entities = new ObservableBuffer[Entity];
  
  val entityGroup = new Pane;
  val particleGroup = new Pane;
  
  def addEntity(e : Entity) : Unit = {
    _entities += e;
    e.world = this;
    
    if (e.node != null) 
      entityGroup.children.add(e.node);
  }
  
  def removeEntity(e : Entity) : Unit = {
    _entities -= e;
    e.world = null;
    
    if (e.node != null)
      entityGroup.children.remove(e.node);
  }
  
  def addParticle(node : Node, lifetime : Duration, anim : Animation = null) : AbstractParticle = {
    if (particleGroup == null)
      return null;
    
    val particle = new Particle(this, node);
    particleGroup.children.add(node);
    
    val killTimer = new PauseTransition(lifetime) {
      onFinished = handle { particleGroup.children.remove(node) };
    };
    
    if (anim == null) {
      killTimer.play();
    }
    else {
      val combined = new ParallelTransition(node, Seq(anim, killTimer));
      combined.play();
    }
    return particle;
  }
  
  def getEntitiesAt(x : Int, y : Int) = _entities.filter((e) => {e.x == x && e.y == y});
  
  def getEntitiesIn(region : Region) = _entities.filter(e => region.contains(e.x, e.y));
  
  def getEntitiesById(id : String) = _entities.filter(_.id == id);
  
  def makeEntityId : String = Random.nextLong.toString;

  val width = tileGrid.width;
  val height = tileGrid.height;
  val area = tileGrid.area;
  
  def findPath(x1 : Int, y1 : Int, x2 : Int, y2 : Int) : Queue[(Int, Int)] = {
    val startId = (x1, y1);
    val endId = (x2, y2);
    
    val path = tileGrid.navMap.findPath(startId, endId);
    debugDisplayPath(path);
    if (path == null)
      println("No path: failed to find path.");
    return path;
  }
  
  def getSpotNextTo(x : Int, y : Int) : (Int, Int) = {
    val connections = tileGrid.navMap.connections((x, y));
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
    }
  }
  
  def toJsonString = null;
  
  override def toString() = s"$name\n$tileGrid\nentities: " + _entities.mkString(", ");
  
  private def debugDisplayPath(path : Queue[(Int, Int)]) = {
    if (minirpg.global_debugPaths) {
      if (path != null) {
        var i = 0;
        val length = path.length;
        for (point <- path) {
          val c = new Circle() {
            centerX = point._1 * tileGrid.tileWidth + tileGrid.tileWidth / 2;
            centerY = point._2 * tileGrid.tileHeight + tileGrid.tileHeight / 2;
            radius = (tileGrid.tileWidth / 4) min (tileGrid.tileHeight / 4);
            fill = Color.rgb(255 * (length - i - 1) / length, 255 * (i + 1) / length, 0);
          };
          val fadeOut = new FadeTransition(new Duration(Duration(2000)), c) {
            fromValue = 1.0;
            toValue = 0.0;
          };
          addParticle(c, new Duration(Duration(4000)), fadeOut);
          i = i + 1;
        }
      }
    }
  }
}