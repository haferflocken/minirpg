package minirpg.model.overworld

import minirpg._
import minirpg.collection.immutable.Graph
import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.canvas.Canvas
import scalafx.scene.SnapshotParameters
import scala.collection.mutable
import minirpg.model.Region
import minirpg.model.Canvasable

class Terrain(
    val grid : Vector[Vector[Double]],
    val gradient : Vector[Vector[(Double, Double)]],
    val waterLevel : Double) extends Canvasable {
  
  val width = grid.length;
  val height = grid(0).length;
  
  val minHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 min l.foldLeft(l(0))((b2, n) => b2 min n)));
  val maxHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 max l.foldLeft(l(0))((b2, n) => b2 max n)));
  val heightRange = maxHeight - minHeight;
  
  val minSlope = gradient.foldLeft(gradient(0)(0)._1)((b1, l) => (b1 min l.foldLeft(l(0)._1)((b2, n) => b2 min n._1 min n._2)));
  val maxSlope = gradient.foldLeft(gradient(0)(0)._1)((b1, l) => (b1 max l.foldLeft(l(0)._1)((b2, n) => b2 max n._1 max n._2)));
  val slopeRange = maxSlope - minSlope;
  
  val navMap : Graph[(Int, Int)] = {
    val conMap = new mutable.HashMap[(Int, Int), Iterable[((Int, Int), Int)]];
    for (i <- 0 until width; j <- 0 until height if grid(i)(j) > waterLevel) {
      var cons : List[((Int, Int), Int)] = Nil;
      if (grid(i)(j) > waterLevel) {
        if (isInBounds(i - 1, j) && grid(i - 1)(j) > waterLevel) {
          val s = gradient(i - 1)(j)._1;
          val weight = (s * s).toInt + 1;
          cons = ((i - 1, j), weight) +: cons;
        }
        if (isInBounds(i, j - 1) && grid(i)(j - 1) > waterLevel) {
          val s = gradient(i)(j - 1)._2;
          val weight = (s * s).toInt + 1;
          cons = ((i, j - 1), weight) +: cons;
        }
        if (isInBounds(i + 1, j) && grid(i + 1)(j) > waterLevel) {
          val s = gradient(i)(j)._1
          val weight = (s * s).toInt + 1;
          cons = ((i + 1, j), weight) +: cons;
          
        }
        if (isInBounds(i, j + 1) && grid(i)(j + 1) > waterLevel) {
          val s = gradient(i)(j)._2;
          val weight = (s * s).toInt + 1;
          cons = ((i, j + 1), weight) +: cons;
        }
      }
      conMap.update((i, j), cons);
    }
    
    new Graph(conMap.keySet.toSet, conMap.toMap);
  }
  
  def mkCanvas(imageWidth : Int, imageHeight : Int) : Canvas =
    mkCanvas(imageWidth, imageHeight, TropicalPainter);
  
  def mkCanvas(imageWidth : Int, imageHeight : Int, painter : TerrainPainter) : Canvas = {
    val canvas = new Canvas(imageWidth, imageHeight);
    val g = canvas.graphicsContext2D;
    val tileWidth = imageWidth.toDouble / width;
    val tileHeight = imageHeight.toDouble / height;
    
    val landHeightRange = (maxHeight - waterLevel);
    val waterHeightRange = (waterLevel - minHeight);
    
    for (i <- 0 until width; j <- 0 until height) {
      if (grid(i)(j) > waterLevel) {
        val percHeight = (grid(i)(j) - waterLevel) / landHeightRange;
        g.fill = painter.paintForLand(percHeight, gradient(i)(j));
      }
      else {
        val percDepth = (waterLevel + grid(i)(j)) / waterHeightRange;
        g.fill = painter.paintForWater(percDepth, gradient(i)(j));
      }
      val x = i * imageWidth / width;
      val y = j * imageHeight / height;
      g.fillRect(x, y, tileWidth, tileHeight);
    }
    
    return canvas;
  }
  
  def mkImage(imageWidth : Int, imageHeight : Int, painter : TerrainPainter = TropicalPainter) : Image = {
    val canvas = mkCanvas(imageWidth, imageHeight, painter);
    return canvas.snapshot(new SnapshotParameters, new WritableImage(imageWidth, imageHeight));
  }
  
  def crop(rX : Int, rY : Int, rWidth : Int, rHeight : Int) : Terrain = {
    if (rX == 0 && rY == 0 && rWidth == width && rHeight == height)
      return this;
    val cGrid = grid.slice(rX, rX + rWidth).map(v => v.slice(rY, rY + rHeight));
    val cGrad = gradient.slice(rX, rX + rWidth).map(v => v.slice(rY, rY + rHeight));
    return new Terrain(cGrid, cGrad, waterLevel);
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
    region.coords.forall((c : (Int, Int)) => isLand(c._1 + region.centerX, c._2 + region.centerY));
  
}

object Terrain {
  
  def mkRandomTerrain(gridSize : Int, maxHeight : Double, waterLevel : Double) : Terrain = {
    val gridBuff = new Array[Double](gridSize * gridSize);
    
    // Create initial random seed values.
    val featureSize = gridSize / 8;
    for (j <- Range(0, gridSize, featureSize); i <- Range(0, gridSize, featureSize)) {
      setSample(gridBuff, gridSize, i, j, rand);
    }
    
    // Diamond-square it.
    var sampleSize = featureSize;
    var scale = maxHeight;
    do {
      diamondSquareStep(gridBuff, gridSize, sampleSize, scale);
      sampleSize /= 2;
      scale /= 2.0;
    } while (sampleSize > 1);
    
    // Find the gradient.
    val gradientBuff = new Array[(Double, Double)](gridBuff.length);
    for (j <- 0 until gridSize; i <- 0 until gridSize) {
      val center = sample(gridBuff, gridSize, i, j);
      val bottom = sample(gridBuff, gridSize, i, j + 1);
      val right = sample(gridBuff, gridSize, i + 1, j);
      
      val bottomSlope = bottom - center;
      val rightSlope = right - center;
      setSample(gradientBuff, gridSize, i, j, (rightSlope, bottomSlope));
    }
    
    // Make the terrain.
    val unrolledGrid = gridBuff.toVector;
    val grid = unrolledGrid.grouped(gridSize).toVector;
    val unrolledGradient = gradientBuff.toVector;
    val gradient = unrolledGradient.grouped(gridSize).toVector;
    return new Terrain(grid, gradient, waterLevel);
  }
  
  private def diamondSquareStep(gridBuff : Array[Double], gridSize : Int, sideLength : Int, scale : Double) : Unit = {
    val halfLength = sideLength / 2;
    
    // Square step.
    for (j <- Range(0, gridSize, sideLength); i <- Range(0, gridSize, sideLength)) {
      val a = sample(gridBuff, gridSize, i, j);
      val b = sample(gridBuff, gridSize, i + sideLength, j);
      val c = sample(gridBuff, gridSize, i, j + sideLength);
      val d = sample(gridBuff, gridSize, i + sideLength, j + sideLength);
      
      val e = (a + b + c + d) / 4.0 + rand * scale * sideLength;
      setSample(gridBuff, gridSize, i + halfLength, j + halfLength, e);
    }
    
    // Diamond step.
    for (j <- Range(0, gridSize, sideLength); i <- Range(0, gridSize, sideLength)) {
      sampleDiamond(gridBuff, gridSize, i + halfLength, j, halfLength, rand * scale * sideLength);
      sampleDiamond(gridBuff, gridSize, i, j + halfLength, halfLength, rand * scale * sideLength);
    }
  }
  
  private def sample[T](gridBuff : Array[T], gridSize : Int, x : Int, y : Int) : T = 
    gridBuff((x & (gridSize - 1)) + (y & (gridSize - 1)) * gridSize);
  
  private def setSample[T](gridBuff : Array[T], gridSize : Int, x : Int, y : Int, value : T) = 
    gridBuff((x & (gridSize - 1)) + (y & (gridSize - 1)) * gridSize) = value;
  
  private def sampleDiamond(gridBuff : Array[Double], gridSize : Int, x : Int, y : Int, halfLength : Int, value : Double) : Unit = {
    val a = sample(gridBuff, gridSize, x - halfLength, y);
    val b = sample(gridBuff, gridSize, x + halfLength, y);
    val c = sample(gridBuff, gridSize, x, y - halfLength);
    val d = sample(gridBuff, gridSize, x, y + halfLength);
    setSample(gridBuff, gridSize, x, y, (a + b + c + d) / 4.0 + value);
  }
  
  private def rand : Double = Math.random * 2.0 - 1.0;
}
