package minirpg.model.overworld

import scalafx.scene.image.Image
import scalafx.scene.canvas.Canvas
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.WritableImage
import scalafx.scene.image.ImageView
import scala.collection.mutable
import scala.collection.immutable
import scala.collection.immutable.Queue
import scalafx.scene.paint.Color
import minirpg.model.world.World
import minirpg.loaders.WorldLoader
import minirpg.model.Region
import minirpg.ui.ResizableCanvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Paint
import minirpg.model.overworld.terrain.{Terrain}

class Overworld(
    val terrain : Terrain,
    val worlds : Map[World, (Region, Vector[Landmark])],
    val artillery : Landmark,
    val artilleryInnerRadius : Int,
    private var _artilleryOuterRadius : Int) {
  
  val width = terrain.width;
  val height = terrain.height;
  val allLandmarks = worlds.values.map(_._2).toVector.flatten;
  val navMap = {
    var out = terrain.navMap;
    
    for ((w, (r, ls)) <- worlds) {
      // Remove world regions from the navMap.
      for (p1 <- r.coords; p2 <- r.coords) {
        if (p1 != p2) {
          out = out.removeConnections((p1, p2));
        }
      }
      
      // Add connections between each world's landmarks.
      for (i <- 0 until ls.length; j <- i until ls.length) {
        val l1 = ls(i);
        val l2 = ls(j);
        out = out.addConnections((l1.coords, l2.coords, 1));
      }
    }
    
    out;
  };
  
  val roads : Map[(Landmark, Landmark), Vector[(Int, Int)]] = {
    // Find the as-the-crow flies distance between all of the landmarks.
    val distances = new mutable.HashMap[(Landmark, Landmark), Double];
    for (i <- 0 until allLandmarks.length; l1 = allLandmarks(i)) {
      for (j <- i + 1 until allLandmarks.length; l2 = allLandmarks(j)) {
        val dX = l1.x - l2.x;
        val dY = l1.y - l2.y;
        val dist = Math.sqrt(dX * dX + dY * dY);
        distances((l1, l2)) = dist;
      }
    }
    
    // Find the average distance between the landmarks.
    val avgDist = distances.foldLeft(0.0)((b, d) => b + d._2) / distances.size;
    
    // Grab the landmarks with less than the average distance to each landmark.
    val closest = new mutable.HashMap[Landmark, List[Landmark]] ++= allLandmarks.map((_, Nil));
    for (((l1, l2), dist) <- distances if dist < avgDist) {
      closest(l1) = l2 +: closest(l1);
    }
    
    // Find the paths through the terrain sequentially so the paths can affect each other.
    val closestCoords = closest.map(p => (p._1.coords, p._2.map(_.coords))).toMap;
    val pathBuff = new mutable.HashMap[((Int, Int), (Int, Int)), Queue[(Int, Int)]];
    var navMap = this.navMap;
    for ((a, bs) <- closestCoords; b <- bs) {
      val path = navMap.findPath(a, b);
      if (path != null) {
        pathBuff += (((a, b), path));
        
        val cons = navMap.connections;
        val newCons = cons.map(x => {
          val (a, bs) = (x._1, x._2);
          if (path contains a) {
            (a, bs.map(y => {
              val (b, weight) = (y._1, y._2);
              if (path contains b) (b, 1);
              else y;
            }));
          }
          else x;
        });
        navMap = navMap.setConnections(newCons);
      }
    }
    
    // Make the paths into roads.
    val coordsToLandmark : Map[(Int, Int), Landmark] = allLandmarks.map(l => ((l.x, l.y), l)).toMap;
    pathBuff.map(p => (
        (coordsToLandmark(p._1._1), coordsToLandmark(p._1._2)),
        p._2.toVector
      )).toMap;
  };
  
  def artilleryOuterRadius = _artilleryOuterRadius;
  
  def artilleryOuterRadius_=(a : Int) : Unit = {
    _artilleryOuterRadius = a;
    _artilleryRegion = mkArtilleryRegion;
  };
  
  def artilleryRegion = _artilleryRegion;
  
  def landmarksInArtillery = allLandmarks.filter(l => _artilleryRegion.contains(l.x, l.y));
  
  private var _artilleryRegion = mkArtilleryRegion;
  private def mkArtilleryRegion : Region = {
    val artilleryDoughnut = Region.ring(artillery.x, artillery.y, _artilleryOuterRadius, _artilleryOuterRadius - artilleryInnerRadius);
    return artilleryDoughnut.clip(0, 0, width, height);
  };

}

