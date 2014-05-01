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

class OverworldScene(val overworld : Overworld) extends Scene with Initializable with Tickable {
  
  fill = Color.BLACK;
  content = new BorderPane {
    val oIWidth = MiniRPGApp.width;
    val oIHeight = oIWidth / 2;
    val overworldImage = overworld.mkImage(oIWidth, oIHeight);
    center = new ImageView(overworldImage);
  };
  
  onMouseMoved = (me : MouseEvent) => {

  };
  
  onMouseClicked = (me : MouseEvent) => {

  };

  onKeyPressed = (ke : KeyEvent) => {

  };
  
  onKeyReleased = (ke : KeyEvent) => {

  };
  
  def tick(delta : Long) : Unit = {

  }
  
  def init : Unit = {

  }
  
}