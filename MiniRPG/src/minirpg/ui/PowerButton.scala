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

class PowerButton(gui : ActorGUI, powerBar : PowerBar, power : Power, actor : Actor, accelKey : KeyCode) extends StackPane {
  
  private val _width = power.uiImage.width();
  private val _height = power.uiImage.height();
  
  val cooldownBackground = Rectangle(_width, _height, Color.rgb(0, 255, 0));
  val cooldownRect = {
    if (power.cooldown <= 0) null;
    else Rectangle(_width, _height, Color.RED);
  };
  val imageView = new ImageView(power.uiImage);
  
  children add cooldownBackground;
  if (cooldownRect != null)
    children add cooldownRect;
  children add imageView;
  
  alignment = Pos.BOTTOM_LEFT;
  onMouseClicked = handle {
    fire;
  };
  if (accelKey == null)
    Tooltip.install(this, power.name);
  else
    Tooltip.install(this, accelKey.getName + ") " + power.name);
  
  private var lastCooldownLevel = Long.MaxValue;
  
  def tick(delta : Long) : Unit = {
    if (cooldownRect != null) {
      val cooldownLevel = actor.powerCooldowns(power);
      if (cooldownLevel != lastCooldownLevel) {
        cooldownRect.height = (_height * cooldownLevel) / power.cooldown;
        val grayness = Math.sqrt(65025 * cooldownLevel / power.cooldown).toInt
        cooldownBackground.fill = Color.rgb(grayness, 255, grayness);
        lastCooldownLevel = cooldownLevel;
      }
    }
  };
  
  def fire() : Unit = {
    if (actor.powerCooldowns(power) <= 0 && power.canUse(actor))
      gui.powerReticle = new PowerReticle(gui, this, actor, power);
  };

}