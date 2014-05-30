package minirpg.ui

import scalafx.Includes.handle
import scalafx.scene.layout.AnchorPane
import scalafx.scene.control.Label
import scalafx.animation.FadeTransition
import scalafx.util.Duration
import scalafx.animation.PauseTransition
import scalafx.animation.SequentialTransition
import minirpg.model.world.Entity
import scala.collection.mutable.ArrayBuffer
import scalafx.scene.control.MenuItem
import scalafx.scene.control.Menu
import scalafx.scene.control.ContextMenu
import scalafx.geometry.Side
import minirpg.model._
import scala.collection.mutable.Subscriber
import scalafx.geometry.Insets
import scalafx.scene.paint.Color
import scalafx.scene.layout.HBox
import scalafx.geometry.Pos
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scalafx.stage.Stage
import javafx.stage.StageStyle
import minirpg.util.Tickable
import minirpg.model.world._
import scala.collection.mutable.HashMap

/**
 * Creates a GUI to control an Actor.
*/
class ActorGUI(actor : Actor) extends AnchorPane with Initializable with Tickable {
  ActorGUI.guis(actor) = this;
  
  def init = {
    vitalsGraph.init;
    wieldMenu.init;
    powerBar.init;
  }
  
  val bottomOffset = 30.0;
  
  var mouseX : Double = 0;
  var mouseY : Double = 0;
  var mouseGridX : Int = 0;
  var mouseGridY : Int = 0;
  onMouseMoved = new EventHandler[MouseEvent] {
    def handle(me : MouseEvent) : Unit = {
      mouseX = me.getSceneX;
      mouseY = me.getSceneY;
      
      if (actor.world != null) {
        mouseGridX = actor.world.tileGrid.screenXToTileX(mouseX);
        mouseGridY = actor.world.tileGrid.screenYToTileY(mouseY);
      }
    }
  }
  onMouseDragged = onMouseMoved.get;
  
  private var _powerReticle : PowerReticle = null;
  def powerReticle = _powerReticle;
  def powerReticle_=(pR : PowerReticle) = {
    if (_powerReticle != null) {
      children.remove(_powerReticle);
      _powerReticle.resetCreatorAppearance;
    }
    _powerReticle = pR;
    if (_powerReticle != null)
      children add _powerReticle;
  }
  
  val vitalsGraph = new VitalsGraph(actor);
  val wieldMenu = new WieldMenu(actor);
  val powerBar = new PowerBar(this, actor);
  
  val bottomBar = new HBox {
    children.addAll(vitalsGraph, wieldMenu, powerBar);
    alignment = Pos.BOTTOM_LEFT;
    fillHeight = false;
  }
  children add bottomBar;
  AnchorPane.setLeftAnchor(bottomBar, 0.0);
  AnchorPane.setBottomAnchor(bottomBar, bottomOffset);
  
  /**
   * Tick the gui, because that might be useful.
   */
  def tick(delta : Long) : Unit = {
    if (_powerReticle != null)
      _powerReticle tick delta;
    powerBar tick delta;
  }
  
  /**
   * Pop up a text string.
   */
  def showPopup(text : String, x : Double, y : Double) : Unit = {
    val popup = new Label(text) {
      layoutX = x;
      layoutY = y;
      background = FXUtils.makeSFXBackground(Color.LIGHTGRAY);
      border = FXUtils.makeSFXBorder(Color.BLACK);
      padding = Insets(4, 4, 4, 4)
    };
    val fadeIn = new FadeTransition(new Duration(Duration(150))) {
      fromValue = 0.0f;
      toValue = 1.0f;
    };
    val wait = new PauseTransition(new Duration(Duration(3500)));
    val fadeOut = new FadeTransition(new Duration(Duration(150))) {
      fromValue = 1.0f;
      toValue = 0.0f;
    };
    val popupAnim = new SequentialTransition(popup, Seq(fadeIn, wait, fadeOut)) {
      onFinished = handle {
        children.remove(popup);
      }
    };
    children.add(popup);
    popupAnim.play();
  }

}

object ActorGUI {
  
  val guis = new HashMap[Actor, ActorGUI];
  
}