package minirpg.model.world

import minirpg._
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.image.ImageView
import minirpg.collection.mutable.Graph
import minirpg.model._
import scala.Array.canBuildFrom
import scala.collection.mutable

class TileGrid(
    _grid : Array[Array[Int]],
    tileMap : Map[Int, Image],
    val tileWidth : Int,
    val tileHeight : Int)
    extends Savable {
  
  def this(grid : Array[Array[Int]], tileMap : Map[Int, Image], tileSize : Int) = 
    this(grid, tileMap, tileSize, tileSize);
  
  def this(width : Int, height : Int, tileMap : Map[Int, Image], tileWidth : Int, tileHeight : Int) = 
    this(Array.ofDim[Int](width, height), tileMap, tileWidth, tileHeight);
  
  def this(width : Int, height : Int, tileMap : Map[Int, Image], tileSize : Int) =
    this(width, height, tileMap, tileSize, tileSize);
  
  private val tileGrid = _grid.map(_.map(_.abs));
  private val collisionGrid = _grid.map(_.map((i : Int) => if (i < 0) true else false));
  
  val width = tileGrid.length;
  val height = tileGrid(0).length;
  val area = width * height;
  
  val tiles = tileMap.values;
  
  val pixelWidth = width * tileWidth;
  val pixelHeight = height * tileHeight;
  
  lazy val compositeImage = new WritableImage(pixelWidth, pixelHeight) {
    println("Building composite image of TileGrid");
    for(x <- 0 to TileGrid.this.width - 1) {
      for (y <- 0 to TileGrid.this.height - 1) {
        val im = tileMap(tileGrid(x)(y));
        if (im != null) {
          val pRMaybe = im.pixelReader;
          if (!pRMaybe.isEmpty) {
            pixelWrit.setPixels(
              x * tileWidth, y * tileHeight, tileWidth, tileHeight, pRMaybe.get, 0, 0);
          }
        }
      }
    }
  }
  
  lazy val node = new ImageView(compositeImage);
  
  val navMap : Graph[(Int, Int)] = {
    val nodes = new mutable.HashSet[(Int, Int)];
    for (x <- 0 until width; y <- 0 until height if !isSolid(x, y)) {
      nodes += ((x, y));
    }
    
    val connections = new mutable.HashMap[(Int, Int), Iterable[((Int, Int), Int)]];
    for(node <- nodes; x = node._1; y = node._2; if !isSolid(x, y)) {
      connections(node) = getConnections(x, y);
    }
    
    new Graph(nodes.toSet, connections.toMap);
  }
  
  def isInBounds(x : Int, y : Int) : Boolean = (x > -1 && y > -1 && x < width && y < height);
  
  def getConnections(x : Int, y : Int) : Iterable[((Int, Int), Int)] = {
    val neighbors = Vector((x, y - 1), (x, y + 1), (x - 1, y), (x + 1, y));
    return for (n <- neighbors if isInBounds(n._1, n._2) if !isSolid(n._1, n._2)) yield ((n._1, n._2), 1);
  }
  
  def tileAt(x : Int, y : Int) : Image = tileMap(tileGrid(x)(y));
  
  def isSolid(x : Int, y : Int) : Boolean = collisionGrid(x)(y);
  
  def screenToTileCoords(screenX : Double, screenY : Double) : (Int, Int) = (screenXToTileX(screenX), screenYToTileY(screenY));
  
  def screenXToTileX(screenX : Double) : Int = (screenX / tileWidth).intValue;
  
  def screenYToTileY(screenY : Double) : Int = (screenY / tileHeight).intValue;
  
  def toJsonString() = null;
  
  override def toString() = s"gridDim: $width x $height\ntileDim: $tileWidth x $tileHeight\ngrid: " + _grid.toPrettyString;
  
}