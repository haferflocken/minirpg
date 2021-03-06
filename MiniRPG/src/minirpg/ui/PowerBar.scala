package minirpg.ui

import minirpg.numsToLetterActionKeys
import minirpg.model._
import minirpg.model.world.Actor.Event._
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
import scalafx.geometry.Orientation
import scala.collection.mutable
import scalafx.scene.image.ImageView
import scalafx.scene.Node

class PowerBar(gui : ActorGUI, actor : Actor) extends TilePane with Subscriber[Actor.Event, Actor] with Initializable with Tickable {
  orientation = Orientation.HORIZONTAL;
  
  private val buttons = new mutable.ArrayBuffer[PowerButton];
  
  def notify(pub : Actor, e : Actor.Event) : Unit = {
    refreshButtons;
  }
  
  def refreshButtons : Unit = {
    children.clear;
    buttons.clear;
    gui.powerReticle = null;
    val powers = actor.powers;
    var i = 0;
    for (p <- powers if p.canBeUsedBy(actor)) {
      i += 1;
      
      val accelKey = numsToLetterActionKeys.getOrElse(i, null);
      val button = new PowerButton(gui, this, p, actor, accelKey);
      buttons += button;
      children.add(button);
      
      if (accelKey != null) {
        val accel = FXUtils.makeAccelerator(accelKey, button.fire);
        scene().getAccelerators.put(accel._1, accel._2);
      }
    }
    prefColumns = powers.length;
  }
  
  /*def refreshBackgrounds : Unit = {
    for ((p, b, r) <- buttons) {
      val (bdr, bg) = borderAndBackground(p.canUse(actor));
      b.border = bdr;
      b.background = bg;
    }
  }
  
  def refreshBackground(power : Power) : Unit = {
    for ((p, b, r) <- buttons if p == power) {
      val (bdr, bg) = borderAndBackground(power.canUse(actor));
      b.border = bdr;
      b.background = bg;
      return;
    }
  }*/
  
  def init = {
    actor.subscribe(
        this,
        _ match {
          case Equip(_) | Unequip(_) | Wield(_) | Unwield(_) | PowerNowUseable(_) | PowerNowNotUseable(_) => true;
          case _ => false;
        });
    refreshButtons;
  }
  
  def tick(delta : Long) : Unit = {
    for (b <- buttons) 
      b.tick(delta);
  }
  
}