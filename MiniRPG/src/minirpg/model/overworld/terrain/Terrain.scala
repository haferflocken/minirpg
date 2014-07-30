package minirpg.model.overworld.terrain

import minirpg._
import minirpg.collection.immutable.HeuristicGraph
import scala.collection.mutable
import minirpg.model.Region
import minirpg.collection.immutable.HeuristicGraph
import minirpg.ui.ResizableCanvas
import scalafx.scene.canvas.GraphicsContext
import minirpg.ui.ResizableCanvas.sfxResizableCanvas2jfx
import scala.Range
import minirpg.ui.overworld.ResizableTerrainRenderer

class Terrain(
    val grid : Vector[Vector[Double]],
    val gradient : Vector[Vector[(Double, Double)]],
    val waterLevel : Double,
    var painter : TerrainPainter) {
  
  val width = grid.length;
  val height = grid(0).length;
  
  val minHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 min l.foldLeft(l(0))((b2, n) => b2 min n)));
  val maxHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 max l.foldLeft(l(0))((b2, n) => b2 max n)));
  val heightRange = maxHeight - minHeight;
  
  val minSlope = gradient.foldLeft(gradient(0)(0)._1)((b1, l) => (b1 min l.foldLeft(l(0)._1)((b2, n) => b2 min n._1 min n._2)));
  val maxSlope = gradient.foldLeft(gradient(0)(0)._1)((b1, l) => (b1 max l.foldLeft(l(0)._1)((b2, n) => b2 max n._1 max n._2)));
  val slopeRange = maxSlope - minSlope;
  
  lazy val navMap : HeuristicGraph[(Int, Int)] = {
    val conMap = new mutable.HashMap[(Int, Int), Map[(Int, Int), Int]];
    for (i <- 0 until width; j <- 0 until height if grid(i)(j) > waterLevel) {
      var cons = new mutable.HashMap[(Int, Int), Int];
      if (grid(i)(j) > waterLevel) {
        if (isInBounds(i - 1, j) && grid(i - 1)(j) > waterLevel) {
          val s = gradient(i - 1)(j)._1;
          val weight = (s * s).toInt + 10;
          cons += (((i - 1, j), weight));
        }
        if (isInBounds(i, j - 1) && grid(i)(j - 1) > waterLevel) {
          val s = gradient(i)(j - 1)._2;
          val weight = (s * s).toInt + 10;
          cons += (((i, j - 1), weight));
        }
        if (isInBounds(i + 1, j) && grid(i + 1)(j) > waterLevel) {
          val s = gradient(i)(j)._1
          val weight = (s * s).toInt + 10;
          cons += (((i + 1, j), weight));
          
        }
        if (isInBounds(i, j + 1) && grid(i)(j + 1) > waterLevel) {
          val s = gradient(i)(j)._2;
          val weight = (s * s).toInt + 10;
          cons += (((i, j + 1), weight));
        }
      }
      conMap.update((i, j), cons.toMap);
    }
    
    new HeuristicGraph(conMap.keySet.toSet, conMap.toMap, HeuristicGraph.manhattanDist, true);
  }
  
  def crop(rX : Int, rY : Int, rWidth : Int, rHeight : Int) : Terrain = {
    if (rX == 0 && rY == 0 && rWidth == width && rHeight == height)
      return this;
    val cGrid = grid.slice(rX, rX + rWidth).map(v => v.slice(rY, rY + rHeight));
    val cGrad = gradient.slice(rX, rX + rWidth).map(v => v.slice(rY, rY + rHeight));
    return new Terrain(cGrid, cGrad, waterLevel, painter);
  }
  
  def isInBounds(x : Int, y : Int) = x >= 0 && y >= 0 && x < width && y < height;
  
  def isInBounds(region : Region) : Boolean = {
    val left = region.leftBound;
    val right = region.rightBound;
    val top = region.topBound;
    val bottom = region.bottomBound;
    return isInBounds(left, top) && isInBounds(right, top) && isInBounds(left, bottom) && isInBounds(right, bottom);
  }
  
  def isLand(x : Int, y : Int) = grid(x)(y) > waterLevel;
  
  def isLand(region : Region) : Boolean =
    region.coords.forall((c : (Int, Int)) => isLand(c._1 + region.anchorX, c._2 + region.anchorY));
  
}
