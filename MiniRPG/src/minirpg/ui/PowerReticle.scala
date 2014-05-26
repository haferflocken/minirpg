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
    region.anchorX = world.tileGrid.screenXToTileX(gui.mouseX);
    region.anchorY = world.tileGrid.screenXToTileX(gui.mouseY);
    layoutX = region.anchorX * world.tileGrid.tileWidth - region.leftmost * world.tileGrid.tileWidth / 2;
    layoutY = region.anchorY * world.tileGrid.tileHeight - region.topmost * world.tileGrid.tileHeight / 2;
  }
  
  onMouseClicked = handle {
    power(actor, world getEntitiesIn region, region);
    gui.powerReticle = null;
  };
  
}