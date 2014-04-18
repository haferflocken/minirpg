package minirpg.ui

import scalafx.Includes.handle
import scalafx.scene.layout.AnchorPane
import scalafx.scene.control.Label
import scalafx.animation.FadeTransition
import scalafx.util.Duration
import scalafx.animation.PauseTransition
import scalafx.animation.SequentialTransition
import minirpg.model.Entity
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

/**
 * Creates a GUI for the game.
 * 
 * Layout is:
 *      Vitals at top left
 *      Equipment at top right
 *      Wield Menu at bottom left
 *      Power Bar at bottom right
 */
class MiniRPGGui(player : Actor) extends AnchorPane with Initializable {
  
  def init = {
    vitalsGraph.init;
    wieldMenu.init;
    powerBar.init;
  }
  
  val bottomOffset = 30.0;
  
  var mouseX : Double = 0;
  var mouseY : Double = 0;
  onMouseMoved = new EventHandler[MouseEvent] {
    def handle(me : MouseEvent) : Unit = {
      mouseX = me.getSceneX;
      mouseY = me.getSceneY;
    }
  }
  onMouseDragged = onMouseMoved.get;
  
  private var _powerReticle : PowerReticle = null;
  def powerReticle = _powerReticle;
  def powerReticle_=(pR : PowerReticle) = {
    if (_powerReticle != null)
      children.remove(_powerReticle);
    _powerReticle = pR;
    if (_powerReticle != null)
      children add _powerReticle;
  }
  
  val vitalsGraph = new VitalsGraph(player);
  val wieldMenu = new WieldMenu(player);
  val powerBar = new PowerBar(this, player);
  
  val bottomBar = new HBox {
    children.addAll(vitalsGraph, wieldMenu, powerBar);
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
  
  /**
   * Show a menu of actions for a vector of entities.
   */
  def showActionMenu(entities : Vector[Entity]) : Unit = {
    val menus = new ArrayBuffer[MenuItem];
    for (t <- entities if (t.useable || t.description != null)) {
      val menu = new Menu(t.name);
      val subItems = new ArrayBuffer[MenuItem];
      if (t.useable) {
        subItems.append(new MenuItem("Use") {
          onAction = handle {
            t.beUsedBy(player);
          }
        });
      }
      if (t.description != null) {
        subItems.append(new MenuItem("Examine") {
          onAction = handle {
            showPopup(t.description, t.node.layoutX(), t.node.layoutY());
          }
        });
      }
      menu.items = subItems;
      menus.append(menu);
    }
    if (menus.length == 0)
      return;
    val cm = new ContextMenu {
      for (m <- menus) items.append(m);
    }
    cm.show(entities(0).node, Side.RIGHT, 0, 0);
  }

}