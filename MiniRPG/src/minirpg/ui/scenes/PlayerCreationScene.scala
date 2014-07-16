package minirpg.ui.scenes

import scalafx.Includes._
import scalafx.scene.Scene
import minirpg.util.Tickable
import minirpg.ui.Initializable
import scalafx.scene.paint.Color
import scalafx.scene.layout.StackPane
import minirpg.ui.MiniRPGApp
import scalafx.scene.text.Text
import minirpg.model.overworld.Overworld
import minirpg.loaders.WorldLoader
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import minirpg.PlayerProfessions
import scalafx.scene.control.Button
import scalafx.scene.control.TextField
import minirpg.entities.actors.Human

/**
 * Make your player.
 */
class PlayerCreationScene extends Scene with Initializable with Tickable {
  
  fill = Color.BLACK;
  content = new VBox {
    minWidth <== PlayerCreationScene.this.width;
    minHeight <== PlayerCreationScene.this.height;
    alignment = Pos.CENTER;
    
    val nameBox = new TextField {
      promptText = "Player name";
    };
    children.add(nameBox);
    
    for (p <- PlayerProfessions.all) {
      val b = new Button(p.name) {
        onAction = handle {
          MiniRPGApp.player = new Human("player", nameBox.text(), null);
          for (g <- p.gear)
            MiniRPGApp.player.equip(g);
          MiniRPGApp.scene = new NewGameSetupScene;
        };
      };
      children.add(b);
    }
  };

  def tick(delta : Long) : Unit = {
  };
  
  def init : Unit = {
  };
  
}