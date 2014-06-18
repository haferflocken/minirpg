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

class NewGameSetupScene extends Scene with Initializable with Tickable {
  
  var ticks = 0;
  
  fill = Color.BLACK;
  content = new StackPane {
    minWidth <== NewGameSetupScene.this.width;
    minHeight <== NewGameSetupScene.this.height;
    content = new Text("- Setting up a new game -") {
      fill = Color.WHITE;
    };
    alignment = Pos.CENTER;
  };

  def tick(delta : Long) : Unit = {
    if (ticks > 1) {
      ticks = -1;
      MiniRPGApp.overworld = Overworld.mkRandomOverworld(200, 100, 18, 6);
      for (l <- MiniRPGApp.overworld.landmarks) {
        WorldLoader.loadJsonFile(l.worldPath);
      }
      
      MiniRPGApp.scene = new OverworldScene(MiniRPGApp.overworld);
    }
    else if (ticks >= 0)
      ticks += 1;
  }
  
  def init : Unit = {
  }
}