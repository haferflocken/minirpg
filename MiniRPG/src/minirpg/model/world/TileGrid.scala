package minirpg.model.world

import minirpg._
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.image.ImageView
import minirpg.collection.immutable.HeuristicGraph
import minirpg.model._
import scala.Array.canBuildFrom
import scala.collection.mutable
import minirpg.collection.immutable.HeuristicGraph
import minirpg.collection.immutable.UndirectedUtils
import scala.collection.immutable.HashSet

class TileGrid(
    cells : Vector[Vector[Int]],
    tileMap : Map[Int, Image],
    val navMap : HeuristicGraph[(Int, Int)],
    val tileWidth : Int,
    val tileHeight : Int,
    val portals : Vector[Portal])
    extends Savable {
  
  val width = cells.length;
  val height = cells(0).length;
  val area = width * height;
  
  val tiles = tileMap.values;
  
  val pixelWidth = width * tileWidth;
  val pixelHeight = height * tileHeight;
  
  lazy val compositeImage = new WritableImage(pixelWidth, pixelHeight) {
    println("Building composite image of TileGrid");
    for(x <- 0 to TileGrid.this.width - 1) {
      for (y <- 0 to TileGrid.this.height - 1) {
        val im = tileMap(cells(x)(y));
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
  
  def isInBounds(x : Int, y : Int) : Boolean = (x > -1 && y > -1 && x < width && y < height);
  
  def tileAt(x : Int, y : Int) : Image = tileMap(cells(x)(y));
  
  def screenToTileCoords(screenX : Double, screenY : Double) : (Int, Int) = (screenXToTileX(screenX), screenYToTileY(screenY));
  
  def screenXToTileX(screenX : Double) : Int = (screenX / tileWidth).intValue;
  
  def screenYToTileY(screenY : Double) : Int = (screenY / tileHeight).intValue;
  
  def mkPortalRegion : Region = {
    val top = portals.minBy(_.overworldY).overworldY;
    val bottom = portals.maxBy(_.overworldY).overworldY;
    val left = portals.minBy(_.overworldX).overworldX;
    val right = portals.maxBy(_.overworldX).overworldX;
    val coords = for (x <- left to right; y <- top to bottom) yield (x, y);
    return new Region(HashSet() ++ coords);
  };
  
  def toJsonString() = null;
  
  override def toString() = s"gridDim: $width x $height\ntileDim: $tileWidth x $tileHeight\ncells: " + cells.toPrettyString;
  
}

object TileGrid {
  
  def apply(grid : Array[Array[Int]], tileMap : Map[Int, Image], tileWidth : Int, tileHeight : Int, portals : Vector[Portal]) : TileGrid = {
    // Extract the cells from the grid.
    val cellBuff = new mutable.ArrayBuffer[Vector[Int]];
    for (x <- Range(0, grid.length, 2)) {
      val columnBuff = new mutable.ArrayBuffer[Int];
      for (y <- Range(0, grid(x).length, 2))
        columnBuff += grid(x)(y);
      cellBuff += columnBuff.toVector;
    }
    val cells = cellBuff.toVector;
    
    // Extract the edges from the grid.
    val edgeBuff = new mutable.ArrayBuffer[((Int, Int), (Int, Int), Int)];
    for (x <- 0 until grid.length) {
      if (x % 2 == 0) {
        // Vertical edges.
        val trueCol = x / 2;
        for (y <- Range(1, grid(x).length, 2)) {
          if (grid(x)(y) == Int.MaxValue) {
            val a = (trueCol, (y - 1) / 2);
            val b = (trueCol, (y + 1) / 2);
            edgeBuff += ((a, b, 1));
          }
        }
      }
      else {
        // Horizontal edges.
        for (y <- 0 until grid(x).length) {
          if (grid(x)(y) == Int.MaxValue) {
            val a = ((x - 1) / 2, y);
            val b = ((x + 1) / 2, y);
            edgeBuff += ((a, b, 1));
          }
        }
      }
    }
    
    // Make a graph from the cells and edges.
    val cellSeq = for (x <- 0 until cells.length; y <- 0 until cells(x).length) yield (x, y);
    val navMap = new HeuristicGraph(cellSeq.toSet, UndirectedUtils.undirectedToDirected(edgeBuff), HeuristicGraph.manhattanDist, true);
    
    return new TileGrid(cells, tileMap, navMap, tileWidth, tileHeight, portals);
  }
  
}