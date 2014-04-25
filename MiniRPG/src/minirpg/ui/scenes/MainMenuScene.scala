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
import minirpg.model.Terrain
import scalafx.scene.image.ImageView

class MainMenuScene extends Scene with Tickable with Initializable {
  val t = Terrain.mkRandomTerrain(256, 100, Double.MinValue);
  
  fill = Color.BLACK;
  content = new VBox {
    content = Vector(
        new Text("MiniRPG") {
          fill = Color.WHITE;
        },
        new Button("New Game") {
          onMouseClicked = handle { MiniRPGApp.scene = new WorldScene(MiniRPGApp.world) };
        },
        new ImageView(t.mkImage(512, 512)));
    minWidth = 800;
    minHeight = 600;
  }
  
  def init : Unit = {
    
  }
  
  def tick(delta : Long) : Unit = {
    
  }

}