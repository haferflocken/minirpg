package minirpg.ui

import minirpg.model._
import scalafx.Includes.handle
import scalafx.scene.layout.GridPane
import scalafx.scene.shape.Rectangle
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.control.Button
import minirpg.util.Tickable
import minirpg.model.world._

class PowerReticle(gui : ActorGUI, creator : PowerBar, val actor : Actor, val power : Power) extends GridPane with Tickable {
  
  val world = actor.world;
  val region = power.mkRegion(0, 0);
  
  opacity = 0.5f;
  for (p <- region.coords) {
    val rect = Rectangle(world.tileGrid.tileWidth, world.tileGrid.tileHeight);
    rect.fill = Color.RED;
    add(rect, p._1 - region.leftmost, p._2 - region.topmost);
  }
  
  def resetCreatorAppearance : Unit = {
    creator.refreshBackground(power);
  }
  
  def tick(delta : Long) : Unit = {
    region.anchorX = gui.mouseGridX;
    region.anchorY = gui.mouseGridY;
    layoutX = (region.anchorX + region.leftmost) * world.tileGrid.tileWidth;
    layoutY = (region.anchorY + region.topmost) * world.tileGrid.tileHeight;
  }
  
  onMouseClicked = handle {
    power(actor, world getEntitiesIn region toVector, region);
    gui.powerReticle = null;
  };
  
}