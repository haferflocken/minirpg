package minirpg.ui

import scalafx.Includes.handle
import scalafx.scene.layout.TilePane
import minirpg.numsToFKeys
import minirpg.model.world.Gear
import scalafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.BackgroundFill
import javafx.event.EventHandler
import javafx.geometry.Insets
import minirpg.model._
import minirpg.gearMap
import scalafx.scene.shape.Arc
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Circle
import scalafx.scene.control.Button
import scalafx.scene.layout.Border
import scalafx.scene.layout.Background
import scala.collection.mutable.Subscriber
import scalafx.geometry.Orientation
import minirpg.model.world.ActorEvent._
import minirpg.model.world._

class WieldMenu(actor : Actor) extends TilePane with Subscriber[ActorEvent, Actor] with Initializable {
  orientation = Orientation.VERTICAL;
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    children.clear;
    val wieldable = actor.equipped.filter(actor canWield _).toSet;
    var i = 0;
    for (g <- wieldable) {
      i += 1;
      val button = new Button("F" + i + ") " + g.name) {
        maxWidth = Double.MaxValue;
        if (actor.isWielding(g)) {
          onAction = handle { actor.unwield(g) };
          border = FXUtils.DefaultActionBorder;
          background = FXUtils.DefaultActionBackground;
          textFill = Color.BLACK;
        } else {
          onAction = handle { actor.wield(g) };
          border = FXUtils.DefaultBorder;
          background = FXUtils.DefaultBackground;
          textFill = Color.BLACK;
        }
      };
      children.add(button);
      val accelKey = numsToFKeys.getOrElse(i, null);
      if (accelKey != null) {
        val accel = FXUtils.makeAccelerator(accelKey, button.fire);
        scene().getAccelerators.put(accel._1, accel._2);
      }
    }
    prefRows = wieldable.size;
  }
  
  def init = {
    actor.subscribe(this, e => e.event == EQUIP || e.event == UNEQUIP || e.event == WIELD || e.event == UNWIELD);
    notify(null, null);
  }
  
}