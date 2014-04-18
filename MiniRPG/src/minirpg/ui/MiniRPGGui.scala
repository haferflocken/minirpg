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
  
  var windowInsets : Insets = Insets.Empty;
  var mouseX : Double = 0;
  var mouseY : Double = 0;
  
  /**
   * A graph that shows the player's vitals.
   */
  val vitalsGraph = new VitalsGraph(player);
  children add vitalsGraph;
  AnchorPane.setLeftAnchor(vitalsGraph, 0.0);
  AnchorPane.setTopAnchor(vitalsGraph, 0.0);
  
  /**
   * A menu which allows the player to equip gear.
   */
  val wieldMenu = new WieldMenu(player);
  children add wieldMenu;
  AnchorPane.setLeftAnchor(wieldMenu, 0.0);
  AnchorPane.setBottomAnchor(wieldMenu, bottomOffset);
  
  /**
   * A list of buttons for using powers.
   */
  val powerBar = new PowerBar(player);
  children add powerBar;
  AnchorPane.setRightAnchor(powerBar, 0.0);
  AnchorPane.setBottomAnchor(powerBar, bottomOffset);
  
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