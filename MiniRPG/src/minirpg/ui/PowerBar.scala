package minirpg.ui

import minirpg.numsToDigitKeys
import minirpg.model._
import minirpg.model.world.ActorEvent._
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
import scalafx.scene.layout.Background
import scalafx.scene.layout.Border
import minirpg.util.Tickable
import minirpg.model.world._

class PowerBar(gui : ActorGUI, actor : Actor) extends TilePane with Subscriber[ActorEvent, Actor] with Initializable with Tickable {
  
  private var cooldownRects : List[(Power, Rectangle)] = Nil;
  private var buttons : List[(Power, Button)] = Nil;
  
  def notify(pub : Actor, e : ActorEvent) : Unit = {
    if (e.event == EQUIP || e.event == UNEQUIP || e.event == WIELD || e.event == UNWIELD)
      refreshButtons;
    else if (e.event == POWER_NOW_USEABLE || e.event == POWER_NO_LONGER_USEABLE)
      refreshBackgrounds;
  }
  
  def refreshButtons : Unit = {
    children.clear;
    cooldownRects = Nil;
    buttons = Nil;
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
      val (bdr, bg) = borderAndBackground(p.canUse(actor));
      val button = new Button(i + ") " + p.name, graphic) {
        maxWidth = Double.MaxValue;
        maxHeight = Double.MaxValue;
        onAction = handle {
          if (actor.powerCooldowns(p) <= 0 && p.canUse(actor)) {
            gui.powerReticle = new PowerReticle(gui, PowerBar.this, actor, p);
            border = FXUtils.DefaultActionBorder;
            background = FXUtils.DefaultActionBackground;
          }
        };
        border = bdr;
        background = bg;
        textFill = Color.BLACK;
      }
      buttons = (p, button) +: buttons;
      children.add(button);
      val accelKey = numsToDigitKeys.getOrElse(i, null);
      if (accelKey != null) {
        val accel = FXUtils.makeAccelerator(accelKey, button.fire);
        scene().getAccelerators.put(accel._1, accel._2);
      }
    }
    prefColumns = powers.length;
  }
  
  def refreshBackgrounds : Unit = {
    for ((p, b) <- buttons) {
      val (bdr, bg) = borderAndBackground(p.canUse(actor));
      b.border = bdr;
      b.background = bg;
    }
  }
  
  def refreshBackground(power : Power) : Unit = {
    for ((p, b) <- buttons if p == power) {
      val (bdr, bg) = borderAndBackground(power.canUse(actor));
      b.border = bdr;
      b.background = bg;
      return;
    }
  }
  
  def init = {
    actor.subscribe(
        this,
        e => e.event == EQUIP ||
             e.event == UNEQUIP ||
             e.event == WIELD || 
             e.event == UNWIELD ||
             e.event == POWER_NOW_USEABLE || 
             e.event == POWER_NO_LONGER_USEABLE);
    refreshButtons;
  }
  
  def tick(delta : Long) : Unit = {
    for ((p, r) <- cooldownRects) {
      r.height = 16 * (p.cooldown - actor.powerCooldowns(p)) / p.cooldown;
    }
  }
  
  private def borderAndBackground(canUse : Boolean) : (Border, Background) = {
    if (canUse)
      return (FXUtils.DefaultBorder, FXUtils.DefaultBackground);
    return (FXUtils.DefaultDisabledBorder, FXUtils.DefaultDisabledBackground);
  }
  
}