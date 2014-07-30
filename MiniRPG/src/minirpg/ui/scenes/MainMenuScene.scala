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
import minirpg.model.overworld.terrain.Terrain
import scalafx.scene.image.ImageView
import minirpg.model.overworld.Overworld
import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos

class MainMenuScene extends Scene with Tickable with Initializable {
  
  fill = Color.BLACK;
  content = new VBox {
    alignment = Pos.CENTER;
    minWidth <== MainMenuScene.this.width;
    minHeight <== MainMenuScene.this.height;
      
    val title = new Text("MiniRPG") {
      fill = Color.WHITE;
    };
    val newGameButton = new Button("New Game") {
      onMouseClicked = handle { MiniRPGApp.scene = new PlayerCreationScene };
    };
    val optionsButton = new Button("Options") {
      onMouseClicked = handle { MiniRPGApp.scene = new OptionsScene };
    };
    val exitButton = new Button("Exit") {
      // TODO
    };
      
    children.addAll(title, newGameButton, optionsButton, exitButton);
  };
  
  def init : Unit = {
    
  }
  
  def tick(delta : Long) : Unit = {
    
  }

}