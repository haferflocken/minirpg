package minirpg.ui

import minirpg.numsToDigitKeys
import minirpg.model._
import minirpg.model.ActorEvent._
import scalafx.Includes._
import scalafx.scene.layout.TilePane
import scala.collection.mutable.Subscriber
import scalafx.scene.control.Button
import scalafx.Includes.handle
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.control.ToggleGroup
import scalafx.scene.shape.Circle
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Rectangle
import scalafx.geometry.Pos

class PowerBar(gui : MiniRPGGui, actor : Actor) extends TilePane with Subscriber[ActorEvent, Actor] with Initializable {
  
  private var cooldownRects : List[(Power, Rectangle)] = Nil;
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    children.clear;
    cooldownRects = Nil;
    gui.powerReticle = null;
    val powers = actor.powers;
    var i = 0;
    for (p <- powers) {
      i += 1;
      val graphic = {
        if (p.cooldown <= 0)
          Circle(2, Color.BLACK);
        else {
          val bgRect = Rectangle(6, 16, Color.GRAY);
          bgRect.stroke = Color.BLACK;
          val fgRect = Rectangle(6, 8, Color.WHITE); 
          fgRect.stroke = Color.BLACK;
          cooldownRects = (p, fgRect) +: cooldownRects;
          new StackPane {
            children.addAll(bgRect, fgRect);
            alignment = Pos.BOTTOM_LEFT;
          }
        }
      }
      val button = new Button(i + ") " + p.name, graphic) {
        maxWidth = Double.MaxValue;
        maxHeight = Double.MaxValue;
        onAction = handle {
          if (actor.powerCooldowns(p) <= 0) {
            gui.powerReticle = new PowerReticle(gui, this, actor, p);
            border = FXUtils.DefaultActionBorder;
            background = FXUtils.DefaultActionBackground;
          }
        };
        border = FXUtils.DefaultBorder;
        background = FXUtils.DefaultBackground;
        textFill = Color.BLACK;
      }
      children.add(button);
      val accelKey = numsToDigitKeys.getOrElse(i, null);
      if (accelKey != null) {
        val accel = FXUtils.makeAccelerator(accelKey, button.fire);
        scene().getAccelerators.put(accel._1, accel._2);
      }
    }
    prefColumns = powers.length;
  }
  
  def init = {
    actor.subscribe(this, e => e.event == EQUIP || e.event == UNEQUIP || e.event == WIELD || e.event == UNWIELD);
    notify(null, null);
  }
  
  def tick(delta : Long) : Unit = {
    for ((p, r) <- cooldownRects) {
      r.height = 16 * (p.cooldown - actor.powerCooldowns(p)) / p.cooldown;
    }
  }
  
}