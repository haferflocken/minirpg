package minirpg.model

import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.canvas.Canvas
import scalafx.scene.SnapshotParameters

class Terrain(
    val grid : Vector[Vector[Double]],
    val waterLevel : Double) {
  
  val width = grid.length;
  val height = grid(0).length;
  
  val minHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 min l.foldLeft(l(0))((b2, n) => b2 min n)));
  val maxHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 max l.foldLeft(l(0))((b2, n) => b2 max n)));
  
  def mkImage(imageWidth : Int, imageHeight : Int) : Image = {
    val canvas = new Canvas(imageWidth, imageHeight);
    val g = canvas.graphicsContext2D;
    val tileWidth = imageWidth.toDouble / width;
    val tileHeight = imageHeight.toDouble / height;
    for (i <- 0 until width; j <- 0 until height) {
      if (grid(i)(j) > waterLevel) {
        val perc = (grid(i)(j) - minHeight) * 100 / (maxHeight - minHeight);
        g.fill = Terrain.colorForPerc(perc);
      }
      else {
        g.fill = Terrain.waterColor;
      }
      val x = i * tileWidth;
      val y = j * tileHeight;
      g.fillRect(x, y, tileWidth, tileHeight);
    }
    return canvas.snapshot(new SnapshotParameters, new WritableImage(imageWidth, imageHeight));
  }
  
}

object Terrain {
  
  val waterColor = Color.CORNFLOWERBLUE;
  
  val cartoColors = Vector[(Double, Color)](
      (90, Color.gray(1.0)),
      (80, Color.gray(0.9)),
      (70, Color.gray(0.8)),
      (60, Color.gray(0.7)),
      (50, Color.gray(0.6)),
      (40, Color.gray(0.5)),
      (30, Color.gray(0.4)),
      (20, Color.gray(0.3)),
      (10, Color.gray(0.2)),
      (0,  Color.gray(0.1)));
  
  def colorForPerc(perc : Double) : Color = {
    for ((p, c) <- cartoColors if perc >= p)
      return c;
    return Color.BLACK;
  }
  
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
    
    val unrolled = gridBuff.toVector;
    val grid = unrolled.grouped(gridSize).toVector;
    return new Terrain(grid, waterLevel);
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
  
  private def sample(gridBuff : Array[Double], gridSize : Int, x : Int, y : Int) : Double = 
    gridBuff((x & (gridSize - 1)) + (y & (gridSize - 1)) * gridSize);
  
  private def setSample(gridBuff : Array[Double], gridSize : Int, x : Int, y : Int, value : Double) = 
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
