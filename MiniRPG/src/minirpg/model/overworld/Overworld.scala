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
import javafx.scene.canvas.GraphicsContext
import scala.collection.immutable.HashSet

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
  
  def mkResizableCanvas : ResizableCanvas = {
    val canvas = terrain.mkResizableCanvas;
    for (((l1, l2), path) <- roads) {
      canvas.layers += new RoadLayer(path, width, height);
    }
    canvas.layers += new WorldsLayer(worlds, width, height);
    return canvas;
  };

}

class RoadLayer(road : Vector[(Int, Int)], overworldWidth : Int, overworldHeight : Int) extends ResizableCanvas.ResizableLayer {
  
  val region = new Region(HashSet() ++ road);
  
  def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit = {
    val tileWidth = canvasWidth.toDouble / overworldWidth;
    val tileHeight = canvasHeight.toDouble / overworldHeight;
    
    for (j <- 0 until road.length) {
      val p = road(j);
      g.setFill(Color.rgb(255 * j / road.length, 0, 0));
      val rX = p._1 * canvasWidth / overworldWidth;
      val rY = p._2 * canvasHeight / overworldHeight;
      g.fillRect(rX, rY, tileWidth, tileHeight);
    }
  };
  
  def isClickableAt(mouseX : Double, mouseY : Double, canvasWidth : Double, canvasHeight : Double) = 
    region.contains((mouseX / canvasWidth).toInt, (mouseY / canvasHeight).toInt);
  
}

class WorldsLayer(worlds : Map[World, (Region, Vector[Landmark])], overworldWidth : Int, overworldHeight : Int)
  extends ResizableCanvas.ResizableLayer {
  
  def draw(g : GraphicsContext, canvasWidth : Double, canvasHeight : Double) : Unit = {
    val halfTileWidth = canvasWidth.toDouble / overworldWidth / 2;
    val halfTileHeight = canvasHeight.toDouble / overworldHeight / 2;
    
    g.setStroke(Color.BLUE);
    g.setFill(Color.WHITE);
    for ((w, (r, ls)) <- worlds) {
      // Fill the background of the polygon.
      val polyX = ls.map(l => l.x.toDouble).toArray;
      val polyY = ls.map(l => l.y.toDouble).toArray;
      g.fillPolygon(polyX, polyY, ls.size);
      
      // Draw the lines of the polygon.
      for (i <- 0 until ls.length; j <- i + 1 until ls.length) {
        val l1 = ls(i);
        val l2 = ls(j);
        val l1X = l1.x * canvasWidth / overworldWidth + halfTileWidth;
        val l1Y = l1.y * canvasHeight / overworldHeight + halfTileHeight;
        val l2X = l2.x * canvasWidth / overworldWidth + halfTileWidth;
        val l2Y = l2.y * canvasHeight / overworldHeight + halfTileHeight;
        g.strokeLine(l1X, l1Y, l2X, l2Y);
      }
    }
  };
  
  def isClickableAt(mouseX : Double, mouseY : Double, canvasWidth : Double, canvasHeight : Double) : Boolean = {
    val mGX = (mouseX / canvasWidth).toInt;
    val mGY = (mouseY / canvasHeight).toInt;
    for ((w, (r, ls)) <- worlds) {
      if (r.contains(mGX, mGY))
        return true;
    }
    return false;
  };
  
}

object Overworld {
  
  def mkRandomOverworld(width : Int, height : Int, numWorlds : Int, numBarrows : Int) : Overworld = {
    val powerOf2 = Math.pow(2, Math.ceil(Math.log(Math.max(width, height)) / Math.log(2))).toInt;
    val terrain = Terrain.mkRandomTerrain(powerOf2, 100.0, 0.0).crop((powerOf2 - width) / 2, (powerOf2 - height) / 2, width, height);
    val terrainDiagonal = Math.sqrt(width * width + height * height).toInt;
    val landmarks = new mutable.HashMap[World, (Region, Vector[Landmark])];
    
    // Place the Necropolis. It consists of a center barrow surrounded by smaller barrows.
    val centerBarrowPath = WorldLoader.nRandomFrom(1, WorldLoader.centerBarrowPaths)(0);
    val centerBarrowWorld = WorldLoader.loadJsonFile(centerBarrowPath);
    val numOuterBarrows = numBarrows - 1;
    val outerBarrowPaths = WorldLoader.nRandomFrom(numOuterBarrows, WorldLoader.outerBarrowPaths);
    val outerBarrowWorlds = outerBarrowPaths.map(p => WorldLoader.loadJsonFile(p));
    
    // Choose a random starting point from which to find a location
    // for the Necropolis, then spiral around it until one is found.
    val necropolisRadius = Math.max(width, height) / 30;
    val necropolisCircle = Region.circle(0, 0, necropolisRadius);
    placeBySpiral(
      region = necropolisCircle,
      startX = (Math.random * (width - necropolisRadius * 2)).toInt + necropolisRadius,
      startY = (Math.random * (height - necropolisRadius * 2)).toInt + necropolisRadius,
      maxRadius = terrainDiagonal,
      accept = (r) => terrain.isInBounds(r) && terrain.isLand(r));
    
    // Make the barrows.
    val centerBarrow = new Landmark(centerBarrowWorld.name, necropolisCircle.anchorX, necropolisCircle.anchorY, centerBarrowPath, 0);
    landmarks += ((centerBarrowWorld, (Region.tile(necropolisCircle.anchorX, necropolisCircle.anchorY), Vector(centerBarrow))));
    
    val angleOffset = Math.random * Math.PI * 2.0;
    for (i <- 0 until numOuterBarrows) {
      val angle = Math.PI * 2.0 * i / numOuterBarrows + angleOffset;
      val x = (necropolisCircle.anchorX + necropolisRadius * Math.cos(angle)).toInt;
      val y = (necropolisCircle.anchorY + necropolisRadius * Math.sin(angle)).toInt;
      val outerBarrow = new Landmark(outerBarrowWorlds(i).name, x, y, outerBarrowPaths(i), 0);
      landmarks += ((outerBarrowWorlds(i), (Region.tile(x, y), Vector(outerBarrow))));
    }
    
    // Place the other landmarks.
    val numRemaining = numWorlds - numBarrows;
    val landmarkPaths = WorldLoader.nRandomFrom(numRemaining, WorldLoader.nonBarrowPaths);
    val landmarkWorlds = landmarkPaths.map(p => WorldLoader.loadJsonFile(p));
    
    for (i <- 0 until numRemaining) {
      val world = landmarkWorlds(i);
      val circle = world.tileGrid.mkPortalRegion;
      circle.anchorX = (Math.random * width).toInt;
      circle.anchorY = (Math.random * height).toInt;
      placeBySpiral(
        region = circle,
        startX = (Math.random * (width - circle.width)).toInt + circle.width / 2,
        startY = (Math.random * (height - circle.height)).toInt + circle.height / 2,
        maxRadius = terrainDiagonal,
        accept = (r) => terrain.isInBounds(r) && terrain.isLand(r) && landmarks.forall(l => !l._2._1.intersects(circle)));
      
      val ls = for(pI <- 0 until world.tileGrid.portals.length) yield {
        val p = world.tileGrid.portals(pI);
        new Landmark(world.name, circle.anchorX + p.overworldX, circle.anchorY + p.overworldY, landmarkPaths(i), pI);
      }
      landmarks += ((world, (circle, ls.toVector)));
    }
    
    return new Overworld(terrain, landmarks.toMap, centerBarrow, necropolisRadius * 2, 50);
  };
  
  private def placeBySpiral(region : Region, startX : Int, startY : Int, maxRadius : Int, accept : (Region) => Boolean) : Boolean = {
    region.anchorX = startX;
    region.anchorY = startY;
    if (accept(region)) {
      println("Spiraling skipped because start point was acceptable.");
      return true;
    }
    
    val startTime = System.currentTimeMillis;
    
    val maxTheta = Math.PI * 2.0;
    val initialAngle = {
      val rand = (Math.random * 4).toInt;
      maxTheta * rand / 4;
    }
    for (r <- 1 until maxRadius) {
      var theta = 0.0;
      val thetaStep = maxTheta / (4.0 * r);
      while (theta < maxTheta) {
        val angle = theta + initialAngle;
        region.anchorX = startX + (r * Math.cos(angle)).toInt;
        region.anchorY = startY + (r * Math.sin(angle)).toInt;
        if (accept(region)) {
          val endTime = System.currentTimeMillis;
          val deltaTime = endTime - startTime;
          println(s"Spiraling took $deltaTime ms and found a place for the region offset by ($r, $angle).");
          return true;
        }
        theta += thetaStep;
      }
    }
    
    val endTime = System.currentTimeMillis;
    println("Spiraling took " + (endTime - startTime) + " ms but failed to find a place for the region.");
    return false;
  };
  
}