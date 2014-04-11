package minirpg.model

import minirpg._
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.image.ImageView

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
  
  val compositeImage = new WritableImage(pixelWidth, pixelHeight);
  for(x <- 0 to width - 1) {
    for (y <- 0 to height - 1) {
      val im = tileMap(tileGrid(x)(y));
      if (im != null) {
        val pRMaybe = im.pixelReader;
        if (!pRMaybe.isEmpty) {
          compositeImage.pixelWrit.setPixels(
            x * tileWidth, y * tileHeight, tileWidth, tileHeight, pRMaybe.get, 0, 0);
        }
      }
    }
  }
  
  val node = new ImageView(compositeImage);
  
  def tileAt(x : Int, y : Int) : Image = tileMap(tileGrid(x)(y));
  
  def isSolid(x : Int, y : Int) : Boolean = collisionGrid(x)(y);
  
  def toJsonString() = null;
  
  override def toString() = s"gridDim: $width x $height\ntileDim: $tileWidth x $tileHeight\ngrid: " + _grid.toPrettyString;

}