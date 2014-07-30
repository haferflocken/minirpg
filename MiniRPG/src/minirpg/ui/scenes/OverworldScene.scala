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
import minirpg.ui.overworld.OverworldDisplay

class OverworldScene(val overworld : Overworld) extends Scene with Initializable with Tickable {
  
  val overworldDisplay = new OverworldDisplay(overworld);
  
  fill = Color.BLACK;
  content = new OverworldDisplay(overworld) {
    minWidth <== OverworldScene.this.width;
    minHeight <== OverworldScene.this.height;
    
    /*val oIWidth = MiniRPGApp.width;
    val oIHeight = oIWidth * overworld.height / overworld.width;
    
    center = new AnchorPane {
      // Add the background image (terrain and roads).
      overworldCanvas.width = oIWidth;
      overworldCanvas.height = oIHeight;
      content = overworldCanvas;
      
      // Add the artillery area indicator.
      val artilleryImageWidth = artilleryRegion.width * oIWidth / overworld.width;
      val artilleryImageHeight = artilleryRegion.height * oIHeight / overworld.height;
      val artilleryX =
        (artilleryRegion.anchorX + artilleryRegion.leftmost) * oIWidth / overworld.width;
      val artilleryY =
        (artilleryRegion.anchorY + artilleryRegion.topmost) * oIHeight / overworld.height;
      val artilleryNode = artilleryRegion.mkCanvas(artilleryImageWidth, artilleryImageHeight);
      artilleryNode.mouseTransparent = true;
      AnchorPane.setLeftAnchor(artilleryNode, artilleryX);
      AnchorPane.setTopAnchor(artilleryNode, artilleryY);
      content add artilleryNode;
    };*/
  };
  
  onMouseMoved = (me : MouseEvent) => {

  };
  
  onMouseClicked = (me : MouseEvent) => {

  };

  onKeyPressed = (ke : KeyEvent) => {
  	ke.code match {
  	  case KeyCode.R => MiniRPGApp.scene = new NewGameSetupScene;
  	  case _ => ;
  	}
  };
  
  onKeyReleased = (ke : KeyEvent) => {

  };
  
  def tick(delta : Long) : Unit = {

  }
  
  def init : Unit = {

  }
  
}