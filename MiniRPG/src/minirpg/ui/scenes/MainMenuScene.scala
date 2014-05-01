package minirpg.ui.scenes

import scalafx.Includes._
import scalafx.scene.Scene
import minirpg.ui.Initializable
import scalafx.scene.paint.Color
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.scene.control.Button
import minirpg.ui.MiniRPGApp
import minirpg.util.Tickable
import minirpg.model.overworld.Terrain
import scalafx.scene.image.ImageView
import minirpg.model.overworld.Overworld
import scalafx.scene.layout.StackPane

class MainMenuScene extends Scene with Tickable with Initializable {
  
  fill = Color.BLACK;
  content = new StackPane {
    minWidth = MiniRPGApp.width;
    minHeight = MiniRPGApp.height;
    content = new Button("New Game") {
      onMouseClicked = handle { MiniRPGApp.scene = new NewGameSetupScene };
    }
  }
  
  def init : Unit = {
    
  }
  
  def tick(delta : Long) : Unit = {
    
  }

}