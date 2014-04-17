package minirpg.ui

import scalafx.Includes.handle
import scalafx.scene.layout.HBox
import minirpg.model.Gear
import scalafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.BackgroundFill
import javafx.event.EventHandler
import javafx.geometry.Insets
import minirpg.model._
import minirpg.model.ActorEvent._
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

class WieldMenu(actor : Actor) extends HBox with Subscriber[ActorEvent, Actor] {
  
  actor.subscribe(this, e => e.event == EQUIP || e.event == UNEQUIP || e.event == WIELD || e.event == UNWIELD);
  
  notify(null, null);
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    children.clear;
    val wieldable = actor.equipped.filter(actor canWield _);
    for (g <- wieldable) {
      val button = new Button(g.name) {
        if (actor.isWielding(g)) {
          onAction = handle { actor.unwield(g) };
          border = FXUtils.makeSFXBorder(paint = Color.GRAY, strokeWidths = BorderStroke.THIN);
          background = FXUtils.makeSFXBackground(Color.WHITE);
          textFill = Color.BLACK;
        } else {
          onAction = handle { actor.wield(g) };
          border = FXUtils.makeSFXBorder(Color.BLACK);
          background = FXUtils.makeSFXBackground(Color.LIGHTGRAY);
          textFill = Color.BLACK;
        }
      };
      children.add(button);
    }
  }
  
}