package minirpg.ui.scenes

import minirpg.ui._
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.MouseButton
import scalafx.scene.input.KeyEvent
import minirpg.util.Tickable
import minirpg.model.overworld._
import scalafx.scene.layout.BorderPane
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane
import scalafx.geometry.Pos
import minirpg.loaders.WorldLoader
import scalafx.scene.input.KeyCode
import scalafx.scene.control.Tooltip
import scalafx.scene.shape.Ellipse
import minirpg.model.Region

class OverworldScene(val overworld : Overworld) extends Scene with Initializable with Tickable {
  
  fill = Color.BLACK;
  content = new BorderPane {
    val oIWidth = MiniRPGApp.width;
    val oIHeight = oIWidth * overworld.height / overworld.width;
    val overworldImage = overworld.mkImage(oIWidth, oIHeight);
    center = new AnchorPane {
      // Add the background image (terrain and roads).
      content = new ImageView(overworldImage);
      
      // Add the clickable landmark nodes.
      val tileWidth = oIWidth / overworld.width;
      val tileHeight = oIHeight / overworld.height;
      val xOffset = (-Landmark.Image.width() + tileWidth) / 2;
      val yOffset = (-Landmark.Image.height() + tileHeight) / 2;
      
      for (l <- overworld.landmarks) {
        val landmarkNode = new ImageView(Landmark.Image) {
          onMouseClicked = (me : MouseEvent) => {
            println(l.toString);
            val world = WorldLoader.loadJsonFile(l.worldPath);
            MiniRPGApp.scene = new WorldScene(world);
          };
        };
        Tooltip.install(landmarkNode, l.name);
        AnchorPane.setLeftAnchor(landmarkNode, l.x * oIWidth / overworld.width + xOffset);
        AnchorPane.setTopAnchor(landmarkNode, l.y * oIHeight / overworld.height + yOffset);
        content add landmarkNode;
      }
      
      // Add the artillery area indicator.
      val artilleryRegion = overworld.artilleryRegion;
      val artilleryImageWidth = artilleryRegion.width * oIWidth / overworld.width;
      val artilleryImageHeight = artilleryRegion.height * oIHeight / overworld.height;
      val artilleryX = overworld.artillery.x * oIWidth / overworld.width - artilleryImageWidth / 2;
      val artilleryY = overworld.artillery.y * oIHeight / overworld.height - artilleryImageHeight / 2;
      val artilleryNode = artilleryRegion.mkCanvas(artilleryImageWidth, artilleryImageHeight);
      artilleryNode.mouseTransparent = true;
      AnchorPane.setLeftAnchor(artilleryNode, artilleryX);
      AnchorPane.setTopAnchor(artilleryNode, artilleryY);
      content add artilleryNode;
    };
  };
  
  onMouseMoved = (me : MouseEvent) => {

  };
  
  onMouseClicked = (me : MouseEvent) => {

  };

  onKeyPressed = (ke : KeyEvent) => {
	if (ke.code == KeyCode.R) {
	  MiniRPGApp.scene = new NewGameSetupScene;
	}
  };
  
  onKeyReleased = (ke : KeyEvent) => {

  };
  
  def tick(delta : Long) : Unit = {

  }
  
  def init : Unit = {

  }
  
}