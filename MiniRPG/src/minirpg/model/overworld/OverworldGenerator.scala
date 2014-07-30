package minirpg.model.overworld

import collection.mutable
import minirpg.model.overworld.terrain.{Terrain, TerrainGenerator}
import minirpg.model.world.World
import minirpg.model.Region
import minirpg.loaders.WorldLoader

object OverworldGenerator {

  def mkRandomOverworld(width : Int, height : Int, numWorlds : Int, numBarrows : Int) : Overworld = {
    val powerOf2 = Math.pow(2, Math.ceil(Math.log(Math.max(width, height)) / Math.log(2))).toInt;
    val terrain = TerrainGenerator.mkRandomTerrain(powerOf2, 100.0, 0.0).crop((powerOf2 - width) / 2, (powerOf2 - height) / 2, width, height);
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