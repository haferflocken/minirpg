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
import scala.collection.mutable.Subscriber
import scalafx.scene.control.Button

class WorldScene(val world : World, val entryPortalId : Int) extends Scene with Initializable with Tickable with Subscriber[Actor.Event, Actor] {
  
  // Place the player at the portal they entered through.
  val player = MiniRPGApp.player;
  player.x = world.tileGrid.portals(entryPortalId).gridX;
  player.y = world.tileGrid.portals(entryPortalId).gridY;
  world.addEntity(player);
  
  // Get notified when the player dies.
  player.subscribe(this, _ match { case Actor.Event.Die => true; case _ => false } );
  
  // Build the user interface.
  val gui = new ActorGUI(player) {
    maxWidth <== WorldScene.this.width;
    maxHeight <== WorldScene.this.height;
  };
  val inspector = new ActorInspector(this, player) {
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
  
  // Hold on to mouse information.
  onMouseMoved = (me : MouseEvent) => {
    gui.mouseX = me.x;
    gui.mouseY = me.y;
  };
  
  onMouseClicked = (me : MouseEvent) => {
    val tileCoords = world.tileGrid.screenToTileCoords(me.x, me.y);
    // TODO
  };

  onKeyPressed = (ke : KeyEvent) => {
    // Tab toggles the control interface versus the inspector interface.
    if (ke.code == KeyCode.TAB) {
      toggleGUIAndInspector;
    }
  };
  
  onKeyReleased = (ke : KeyEvent) => {

  };
  
  def toggleGUIAndInspector : Unit = {
    if (contentStack.children.contains(gui)) {
      contentStack.children.remove(gui);
      contentStack.children.add(inspectorPane);
      inspector.enableUpdates;
    }
    else {
      contentStack.children.remove(inspectorPane);
      contentStack.children.add(gui);
      inspector.disableUpdates;
    }
  };
  
  def tick(delta : Long) : Unit = {
    world tick delta;
    gui tick delta;
  };
  
  def init : Unit = {
    gui.init;
  };
  
  // Called when the player dies. The GUI needs to be removed and a return to main menu button shown.
  def notify(pub : Actor, event : Actor.Event) : Unit = {
    // Remove the GUI.
    contentStack.children.remove(gui);
    contentStack.children.remove(inspectorPane);
    
    // Add a congrats you died message and an option to return to the main menu.
    val mainMenuButton = new Button("You died!\nClick to return to the main menu.") {
      onAction = handle { MiniRPGApp.scene = new MainMenuScene; };
    };
    val centerPane = new StackPane {
      alignment = Pos.CENTER;
      content = mainMenuButton;
      maxWidth <== WorldScene.this.width;
      maxHeight <== WorldScene.this.height;
    };
    contentStack.children.add(centerPane);
  };
  
}