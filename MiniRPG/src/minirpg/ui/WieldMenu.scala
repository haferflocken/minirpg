package minirpg.ui

import scalafx.Includes.handle
import scalafx.scene.layout.HBox
import minirpg.model.Gear
import scalafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.BackgroundFill
import javafx.event.EventHandler
import javafx.geometry.Insets
import minirpg.model.Actor
import minirpg.gearMap
import minirpg.util.FXUtils
import scalafx.scene.shape.Arc
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Circle
import scalafx.scene.control.Button
import scalafx.scene.layout.Border
import scalafx.scene.layout.Background

class WieldMenu(val player : Actor) extends HBox {
  
  refresh;
  
  def refresh : Unit = {
    children.clear;
    val wieldable = player.equipped.filter(player canWield _);
    for (g <- wieldable) {
      val button = new Button(g.name) {
        if (player.isWielding(g)) {
          onAction = handle { player.unwield(g) };
          border = FXUtils.makeSFXBorder(paint = Color.WHITE, strokeWidths = BorderStroke.MEDIUM);
          background = FXUtils.makeSFXBackground(Color.DARKGRAY);
          textFill = Color.WHITE;
        }
        else {
          onAction = handle { player.wield(g) };
          border = FXUtils.makeSFXBorder(Color.WHITE);
          background = FXUtils.makeSFXBackground(Color.BLACK);
          textFill = Color.WHITE;
        }
      };
      children.add(button);
    }
  }
  
}