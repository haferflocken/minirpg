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

class OptionsScene extends Scene with Initializable with Tickable {
  
  fill = Color.BLACK;
  content = new StackPane {
    minWidth <== OptionsScene.this.width;
    minHeight <== OptionsScene.this.height;
    alignment = Pos.CENTER;
  };

  def tick(delta : Long) : Unit = {
  };
  
  def init : Unit = {
  };
  
}