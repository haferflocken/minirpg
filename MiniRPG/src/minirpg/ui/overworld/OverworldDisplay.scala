package minirpg.ui.overworld

import scalafx.Includes._
import minirpg.model.overworld.{Overworld, Landmark}
import minirpg.model.overworld.terrain.{Terrain}
import scalafx.scene.layout.{StackPane}
import scalafx.geometry.Pos
import minirpg.ui.ResizableCanvas
import scalafx.scene.layout.GridPane
import scalafx.scene.layout.TilePane
import scalafx.scene.paint.Color
import minirpg.ui.FXUtils
import scalafx.scene.Group
import scalafx.scene.layout.AnchorPane
import minirpg.ui.TileGridPane

class OverworldDisplay(val overworld : Overworld) extends StackPane {
  
  alignment = Pos.TOP_LEFT;
  
  val terrainRenderer = new ResizableTerrainRenderer(overworld.terrain);
  val roadRenderers =
    (for (((l1, l2), road) <- overworld.roads) yield new ResizableRoadRenderer(road, overworld.width, overworld.height)).toVector;
  val worldRegionRenderer = new ResizableWorldRegionRenderer(overworld.worlds, overworld.width, overworld.height);
  
  val canvas = new ResizableCanvas {
    renderer = new ResizableCanvas.LayeredRenderer {
      layers += terrainRenderer;
      layers ++= roadRenderers;
      layers += worldRegionRenderer;
    };
    width <== OverworldDisplay.this.width;
    height <== OverworldDisplay.this.height;
  };
  
  val artilleryRegion = overworld.artilleryRegion;
  
  val landmarkDisplays = for (l <- overworld.allLandmarks) yield new LandmarkDisplay(l, artilleryRegion.contains(l.x, l.y));
  val landmarkPane = new TileGridPane(overworld.width, overworld.height) {
    minWidth <== OverworldDisplay.this.width;
    minHeight <== OverworldDisplay.this.height;
    
    for (i <- 0 until landmarkDisplays.length) {
      val l = overworld.allLandmarks(i);
      val disp = landmarkDisplays(i);
      //disp.translateX <== l.x * width() / overworld.width + (-Landmark.ImageWidth + width() / overworld.width) / 2;
      //disp.translateY <== l.y * height() / overworld.height + (-Landmark.ImageHeight + height() / overworld.height) / 2;
      add(disp, l.x, l.y);
    }
  };
  
  // TODO display artillery region
  
  content = canvas;
  children.add(landmarkPane);
  
  /*
      val tileWidth = oIWidth / overworld.width;
      val tileHeight = oIHeight / overworld.height;
      val xOffset = (-Landmark.Image.width() + tileWidth) / 2;
      val yOffset = (-Landmark.Image.height() + tileHeight) / 2;
      
      for (l <- overworld.allLandmarks) {
        // Make the node.
        val landmarkNode = {
          // Nodes that aren't in the artillery region are clickable and display a tooltip.
          if (!artilleryRegion.contains(l.x, l.y)) {
            new ImageView(Landmark.Image) {
              Tooltip.install(this, l.name);
              onMouseClicked = (me : MouseEvent) => {
                println(l.toString);
                val world = WorldLoader.loadJsonFile(l.worldPath);
                MiniRPGApp.scene = new WorldScene(world, l.portalIndex);
              };
            }
          }
          // Nodes in the artillery region display a "destroyed" tooltip and are not clickable.
          else {
            new ImageView(Landmark.DestroyedImage) {
              Tooltip.install(this, l.name + " (Destroyed)");
            }
          }
        };
        // Add the node to this pane.
        AnchorPane.setLeftAnchor(landmarkNode, l.x * oIWidth / overworld.width + xOffset);
        AnchorPane.setTopAnchor(landmarkNode, l.y * oIHeight / overworld.height + yOffset);
        content add landmarkNode;
      }
  */

}