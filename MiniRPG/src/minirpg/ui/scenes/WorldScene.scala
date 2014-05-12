package minirpg.ui.scenes

import minirpg.model._
import minirpg.ui._
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.layout.StackPane
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.MouseButton
import scalafx.scene.input.KeyEvent
import minirpg.util.Tickable
import minirpg.model.world._

class WorldScene(val world : World) extends Scene with Initializable with Tickable {
  
  val player = world.getEntitiesById("player")(0).asInstanceOf[Actor];
  val gui = new ActorGUI(player);
  
  fill = Color.BLACK;
  content = new StackPane {
    children.addAll(world.canvas, world.particleCanvas, world.debugCanvas, gui);
    minWidth = 800;
    minHeight = 600;
  };
  
  onMouseMoved = (me : MouseEvent) => {
    gui.mouseX = me.x;
    gui.mouseY = me.y;
  };
  
  onMouseClicked = (me : MouseEvent) => {
    val tileCoords = world.tileGrid.screenToTileCoords(me.x, me.y);
  };

  onKeyPressed = (ke : KeyEvent) => {

  };
  
  onKeyReleased = (ke : KeyEvent) => {

  };
  
  def tick(delta : Long) : Unit = {
    world tick delta;
    gui tick delta;
  }
  
  def init : Unit = {
    gui.init;
  }
  
}