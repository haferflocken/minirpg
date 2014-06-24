package minirpg.ui

import scalafx.Includes._
import scalafx.scene.layout.StackPane
import minirpg.model.world._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.scene.image.ImageView
import scalafx.geometry.Pos
import scalafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import scala.collection.mutable.Subscriber
import minirpg.model.world.Actor.Event._

class WieldButton(gear : Gear, actor : Actor, accelKey : KeyCode) extends StackPane with Subscriber[Actor.Event, Actor] {
  actor.subscribe(this, _ match { case Wield(_) | Unwield(_) => true; case _ => false });
  
  private val _width = 0;
  private val _height = 0;
  
  val imageView = {
    if (actor.isWielding(gear))
      new ImageView(gear.uiWieldImage);
    else
      new ImageView(gear.uiUnwieldImage);
  }
  children add imageView;
  
  alignment = Pos.BOTTOM_LEFT;
  onMouseClicked = handle {
    fire;
  };
  if (accelKey == null)
    Tooltip.install(this, gear.name);
  else
    Tooltip.install(this, accelKey.getName + ") " + gear.name);
  
  def fire() : Unit = {
    if (actor.isWielding(gear))
      actor.unwield(gear);
    else
      actor.wield(gear);
  };
  
  def notify(pub : Actor, event : Actor.Event) : Unit = {
    if (actor.isWielding(gear))
      imageView.image = gear.uiWieldImage;
    else 
      imageView.image = gear.uiUnwieldImage;
  };

}