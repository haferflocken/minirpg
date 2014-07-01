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
import scalafx.scene.layout.Pane
import scalafx.geometry.Pos
import scalafx.scene.input.KeyCode

class WorldScene(val world : World) extends Scene with Initializable with Tickable {
  
  val player = world.getEntitiesById("player")(0).asInstanceOf[Actor];
  val gui = new ActorGUI(player) {
    maxWidth <== WorldScene.this.width;
    maxHeight <== WorldScene.this.height;
  };
  val inspector = new ActorInspector(player) {
    maxWidth <== WorldScene.this.width * 3 / 4;
    maxHeight <== WorldScene.this.height * 3 / 4;
  };
  val inspectorPane = new StackPane {
    alignment = Pos.CENTER;
    content = inspector;
    maxWidth <== WorldScene.this.width;
    maxHeight <== WorldScene.this.height;
  };
  val worldPane = new Pane {
    children.addAll(world.tileGrid.node, world.entityGroup, world.particleGroup);
  };
  val contentStack = new StackPane {
    alignment = Pos.TOP_LEFT;
    children.addAll(worldPane, gui);
    minWidth <== WorldScene.this.width;
    minHeight <== WorldScene.this.height;
  };
  
  fill = Color.BLACK;
  content = contentStack;
  
  onMouseMoved = (me : MouseEvent) => {
    gui.mouseX = me.x;
    gui.mouseY = me.y;
  };
  
  onMouseClicked = (me : MouseEvent) => {
    val tileCoords = world.tileGrid.screenToTileCoords(me.x, me.y);
  };

  onKeyPressed = (ke : KeyEvent) => {
    if (ke.code == KeyCode.TAB) {
      if (contentStack.children.contains(gui)) {
        contentStack.children.remove(gui);
        contentStack.children.add(inspectorPane);
      }
      else {
        contentStack.children.remove(inspectorPane);
        contentStack.children.add(gui);
      }
    }
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