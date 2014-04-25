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

class PowerReticle(gui : MiniRPGGui, creator : PowerBar, val actor : Actor, val power : Power) extends GridPane with Tickable {
  
  val world = actor.world;
  val region = power.mkRegion(0, 0);
  
  opacity = 0.5f;
  for (p <- region.coords) {
    val rect = Rectangle(world.tileGrid.tileWidth, world.tileGrid.tileHeight);
    rect.fill = Color.RED;
    add(rect, p._1 + region.width / 2, p._2 + region.height / 2);
  }
  
  def resetCreatorAppearance : Unit = {
    creator.refreshBackground(power);
  }
  
  def tick(delta : Long) : Unit = {
    region.centerX = world.tileGrid.screenXToTileX(gui.mouseX);
    region.centerY = world.tileGrid.screenXToTileX(gui.mouseY);
    layoutX = region.centerX * world.tileGrid.tileWidth - region.width * world.tileGrid.tileWidth / 2;
    layoutY = region.centerY * world.tileGrid.tileHeight - region.height * world.tileGrid.tileHeight / 2;
  }
  
  onMouseClicked = handle {
    power(actor, world getEntitiesIn region, region);
    gui.powerReticle = null;
  };
  
}