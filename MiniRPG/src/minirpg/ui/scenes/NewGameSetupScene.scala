package minirpg.ui.scenes

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
import scala.concurrent._
import scalafx.scene.layout.VBox
import scalafx.scene.control.ProgressIndicator
import minirpg.entities.actors.Human

class NewGameSetupScene extends Scene with Initializable with Tickable {
  
  var overworld : Overworld = null;
  
  fill = Color.BLACK;
  content = new StackPane {
    minWidth <== NewGameSetupScene.this.width;
    minHeight <== NewGameSetupScene.this.height;
    alignment = Pos.CENTER;
    
    content = new VBox {
      alignment = Pos.CENTER;
      spacing = 8;
      
      val progressIndicator = new ProgressIndicator {
        progress = ProgressIndicator.INDETERMINATE_PROGRESS;
        prefWidth = 64;
        prefHeight = 64;
      };
      val title = new Text("Setting up a new game") {
        fill = Color.WHITE;
      };
      children.addAll(progressIndicator, title);
    }
  };

  // Check if the overworld is done yet. This is necessary to avoid blocking the main JavaFX thread.
  def tick(delta : Long) : Unit = {
    // If the overworld is made, make the player and switch to the overworld scene.
    if (overworld != null) {
      MiniRPGApp.overworld = overworld;
      MiniRPGApp.scene = new OverworldScene(overworld);
    }
  };
  
  // On init, start making the overworld on another thread.
  def init : Unit = {
    import ExecutionContext.Implicits.global
    val overworldFuture = Future[Overworld] {
      Overworld.mkRandomOverworld(200, 100, 18, 6);
    }
    overworldFuture onSuccess {
      case overworld => {
        NewGameSetupScene.this.overworld = overworld;
      }
    }
  };
  
}