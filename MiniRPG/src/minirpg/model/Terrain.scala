package minirpg.model

import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.canvas.Canvas
import scalafx.scene.SnapshotParameters

class Terrain(
    val grid : Vector[Vector[Double]],
    val gradient : Vector[Vector[(Double, Double)]],
    val waterLevel : Double) {
  
  val width = grid.length;
  val height = grid(0).length;
  
  val minHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 min l.foldLeft(l(0))((b2, n) => b2 min n)));
  val maxHeight = grid.foldLeft(grid(0)(0))((b1, l) => (b1 max l.foldLeft(l(0))((b2, n) => b2 max n)));
  val heightRange = maxHeight - minHeight;
  
  val minSlope = gradient.foldLeft(gradient(0)(0)._1)((b1, l) => (b1 min l.foldLeft(l(0)._1)((b2, n) => b2 min n._1 min n._2)));
  val maxSlope = gradient.foldLeft(gradient(0)(0)._1)((b1, l) => (b1 max l.foldLeft(l(0)._1)((b2, n) => b2 max n._1 max n._2)));
  val slopeRange = maxSlope - minSlope;
  
  def mkImage(imageWidth : Int, imageHeight : Int) : Image = {
    val canvas = new Canvas(imageWidth, imageHeight);
    val g = canvas.graphicsContext2D;
    val tileWidth = imageWidth.toDouble / width;
    val tileHeight = imageHeight.toDouble / height;
    for (i <- 0 until width; j <- 0 until height) {
      if (grid(i)(j) > waterLevel) {
        val percHeight = (grid(i)(j) - minHeight) / heightRange;
        val grad = gradient(i)(j);
        val percGrad = ((grad._1 max grad._2) - minSlope) / slopeRange;
        g.fill = Terrain.cartoColor(percHeight, percGrad);
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
      (0.9, Color.gray(1.0)),
      (0.8, Color.gray(0.9)),
      (0.7, Color.gray(0.8)),
      (0.6, Color.gray(0.7)),
      (0.5, Color.gray(0.6)),
      (0.4, Color.gray(0.5)),
      (0.3, Color.gray(0.4)),
      (0.2, Color.gray(0.3)),
      (0.1, Color.gray(0.2)),
      (0.0, Color.gray(0.1)));
  
  def cartoGray(perc : Double) : Color = {
    for ((p, c) <- cartoColors if perc >= p)
      return c;
    return Color.BLACK;
  }
  
  val cartoHBs = Vector[(Double, (Double, Double))](
      (0.9, (0, 1.0)),
      (0.8, (30, 1.0)),
      (0.7, (60, 1.0)),
      (0.6, (90, 1.0)),
      (0.5, (120, 1.0)),
      (0.4, (150, 1.0)),
      (0.3, (180, 1.0)),
      (0.2, (210, 1.0)),
      (0.1, (240, 1.0)));
  
  def cartoHB(perc : Double) : (Double, Double) = {
    for ((p, hb) <- cartoHBs if perc >= p)
      return hb;
    return (300.0, 1.0);
  }
  
  def cartoColor(percHeight : Double, percGradient : Double) : Color = {
    val (hue, brightness) = cartoHB(percHeight);
    return Color.hsb(hue, Math.sqrt(1.0 - percGradient), brightness);
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
