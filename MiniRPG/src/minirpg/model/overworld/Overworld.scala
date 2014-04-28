package minirpg.model.overworld

import scalafx.scene.image.Image
import scalafx.scene.canvas.Canvas
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.WritableImage
import scalafx.scene.image.ImageView
import scala.collection.mutable.HashMap
import scala.collection.immutable.Queue
import minirpg.util.Graph
import scalafx.scene.paint.Color

class Overworld(val terrain : Terrain, val landmarks : Vector[Landmark]) {
  
  val width = terrain.width;
  val height = terrain.height;
  
  val roads : Map[(Landmark, Landmark), Vector[(Int, Int)]] = {
    // Find the as-the-crow flies distance between all of the landmarks.
    val distances = new HashMap[(Landmark, Landmark), Double];
    for (l1 <- landmarks) {
      for (l2 <- landmarks if l1 != l2) {
        val dX = l1.x - l2.x;
        val dY = l1.y - l2.y;
        val dist = Math.sqrt(dX * dX + dY * dY);
        distances.put((l1, l2), dist);
      }
    }
    
    // Find the average distance between the landmarks.
    val avgDist = distances.foldLeft(0.0)((b, x) => b + x._2) / distances.size;
    
    // Grab the landmarks with less than the average distance to each landmark.
    val closest = new HashMap[Landmark, List[Landmark]] ++= landmarks.map((_, Nil));
    for (((l1, l2), dist) <- distances if dist < avgDist) {
      closest(l1) = l2 +: closest(l1);
    }
    
    // Try to find paths to each through the terrain.
    val paths = new HashMap[(Landmark, Landmark), Queue[Graph[(Int, Int), Null]]];
    for ((l1, ls) <- closest; l2 <- ls) {
      val path = Graph.findPath((l1.x, l1.y), (l2.x, l2.y), terrain.navMap);
      if (path != null) {
        paths.update((l1, l2), path);
        println("found path between " + l1 + " and " + l2);
      }
      else {
        println("no path between " + l1 + " and " + l2);
      }
    }
    
    // Output the roads.
    paths.map(p => (p._1, p._2.map(g => (g.id._1, g.id._2)).toVector)).toMap;
  };
  
  def mkCanvas(imageWidth : Int, imageHeight : Int) : Canvas = {
    val canvas = terrain.mkCanvas(imageWidth, imageHeight);
    val g = canvas.graphicsContext2D;
    
    val tileWidth = imageWidth.toDouble / width;
    val tileHeight = imageHeight.toDouble / height;
    
    var i = 0;
    for (((l1, l2), path) <- roads) {
      i += 1;
      g.fill = Color.rgb(255 * i / roads.size, 0, 0);
      for (p <- path) {
        val rX = p._1 * tileWidth;
        val rY = p._2 * tileHeight;
        g.fillRect(rX, rY, tileWidth, tileHeight);
      }
    }
    
    g.fill = Color.LIME;
    for (l <- landmarks) {
      val lX = l.x * tileWidth;
      val lY = l.y * tileHeight;
      g.fillRect(lX, lY, tileWidth, tileHeight);
    }
    
    return canvas;
  }
  
  def mkImage(imageWidth : Int, imageHeight : Int) : Image = {
    val canvas = mkCanvas(imageWidth, imageHeight)
    return canvas.snapshot(new SnapshotParameters, new WritableImage(imageWidth, imageHeight));
  }

}

object Overworld {
  
  val landmarkImage = new Image("file:res\\sprites\\landmark.png");
  
  def mkRandomOverworld(width : Int, height : Int, numLandmarks : Int) : Overworld = {
    val powerOf2 = Math.pow(2, Math.ceil(Math.log(Math.max(width, height)) / Math.log(2))).toInt;
    val terrain = Terrain.mkRandomTerrain(powerOf2, 100.0, 0.0).crop((powerOf2 - width) / 2, (powerOf2 - height) / 2, width, height);
    
    val landmarkNames = Landmark.nRandomNames(numLandmarks);
    var landmarks = Vector[Landmark]();
    for (i <- 0 until numLandmarks) {
      var x = Math.random * width toInt;
      var y = Math.random * height toInt;
      while (terrain.grid(x)(y) <= terrain.waterLevel) {
        x = Math.random * width toInt;
        y = Math.random * height toInt;
      }
      val landmark = new Landmark(landmarkNames(i), x, y);
      landmarks = landmarks :+ landmark;
    }
    
    return new Overworld(terrain, landmarks);
  }
  
}