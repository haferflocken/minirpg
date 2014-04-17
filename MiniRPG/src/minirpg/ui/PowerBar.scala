package minirpg.ui

import minirpg.model._
import minirpg.model.ActorEvent._
import scalafx.scene.layout.HBox
import scala.collection.mutable.Subscriber
import scalafx.scene.control.Button
import scalafx.Includes.handle

class PowerBar(actor : Actor) extends HBox with Subscriber[ActorEvent, Actor] {
  
  actor.subscribe(this, e => e.event == EQUIP || e.event == UNEQUIP || e.event == WIELD || e.event == UNWIELD);
  
  notify(null, null);
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    children.clear;
    val powers = actor.powers;
    for (p <- powers) {
      val button = new Button(p.name) {
        onMouseClicked = handle {};
      }
      children.add(button);
    }
  }
}