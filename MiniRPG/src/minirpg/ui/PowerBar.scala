package minirpg.ui

import minirpg.numsToDigitKeys
import minirpg.model._
import minirpg.model.ActorEvent._
import scalafx.scene.layout.TilePane
import scala.collection.mutable.Subscriber
import scalafx.scene.control.ToggleButton
import scalafx.Includes.handle
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.control.ToggleGroup

class PowerBar(actor : Actor) extends TilePane with Subscriber[ActorEvent, Actor] with Initializable {
  
  private val toggleGroup = new ToggleGroup;
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    children.clear;
    val powers = actor.powers;
    var i = 0;
    for (p <- powers) {
      i += 1;
      val button = new ToggleButton(i + ") " + p.name) {
        maxWidth = Double.MaxValue;
        maxHeight = Double.MaxValue;
        onMouseClicked = handle {};
        //border = FXUtils.makeSFXBorder(Color.BLACK);
        //background = FXUtils.makeSFXBackground(Color.LIGHTGRAY);
        //textFill = Color.BLACK;
      }
      button.toggleGroup = toggleGroup;
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
}