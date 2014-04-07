package minirpg.model

import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.image.ImageView

class TileGrid(
    grid : Array[Array[Int]],
    tiles : Map[Int, Image],
    val tileWidth : Int,
    val tileHeight : Int)
    extends Savable {
  
  def this(grid : Array[Array[Int]], tiles : Map[Int, Image], tileSize : Int) = 
    this(grid, tiles, tileSize, tileSize);
  
  val width = grid.length;
  val height = grid(0).length;
  val area = width * height;
  
  val pixelWidth = width * tileWidth;
  val pixelHeight = height * tileHeight;
  
  val compositeImage = new WritableImage(pixelWidth, pixelHeight);
  for(x <- 0 to width - 1) {
    for (y <- 0 to height - 1) {
      val pRMaybe = tiles(grid(x)(y)).pixelReader;
      if (!pRMaybe.isEmpty) {
        compositeImage.pixelWrit.setPixels(
          x * tileWidth, y * tileHeight, tileWidth, tileHeight, pRMaybe.get, 0, 0);
      }
    }
  }
  
  val node = new ImageView(compositeImage);
  
  def tileAt(x : Int, y : Int) : Image = tiles(grid(x)(y));
  
  def toJsonString() = null;

}