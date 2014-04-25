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
  val gui : MiniRPGGui = new MiniRPGGui(player);
  
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

    // Left click should pull up an action menu of what can be done
    // to the entities on the tile.
    if (me.button == MouseButton.SECONDARY) {
      if ((tileCoords._1 - player.x).abs + (tileCoords._2 - player.y).abs <= 1 ) {
        val useTargets = world.getEntitiesAt(tileCoords._1, tileCoords._2).filter(_.node != null);
        if (useTargets.nonEmpty)
          gui.showActionMenu(useTargets);
      }
    }
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