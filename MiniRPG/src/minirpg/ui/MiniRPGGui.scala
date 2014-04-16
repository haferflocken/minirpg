package minirpg.ui

import scalafx.Includes.handle
import scalafx.scene.layout.Pane
import scalafx.scene.text.Text
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

class MiniRPGGui(player : Actor) extends Pane with Subscriber[ActorEvent, Actor] {
  
  player.subscribe(this);
  
  var mouseX : Double = 0;
  var mouseY : Double = 0;
  
  /**
   * A menu which allows the player to equip gear.
   */
  val wieldMenu : WieldMenu = new WieldMenu(player) {
    setMaxWidth(100);
    setMaxHeight(100);
  };
  children.add(wieldMenu);
  
  /**
   * Called when the player publishes an event.
   */
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    if (event.event == ActorEvent.EQUIP || event.event == ActorEvent.UNEQUIP
        || event.event == ActorEvent.WIELD | event.event == ActorEvent.UNWIELD) {
      wieldMenu.refresh;
    }
  }
  
  /**
   * Pop up a text string.
   */
  def showPopup(text : String, x : Double, y : Double) : Unit = {
    val popup = new Text(x, y, text);
    val fadeIn = new FadeTransition(new Duration(Duration(150))) {
      fromValue = 0.0f;
      toValue = 1.0f;
    };
    val wait = new PauseTransition(new Duration(Duration(3000)));
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