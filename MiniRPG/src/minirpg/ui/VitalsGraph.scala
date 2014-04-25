package minirpg.ui

import minirpg.model._
import scala.collection.mutable.Subscriber
import scalafx.scene.layout.GridPane
import scalafx.scene.text.Text
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.geometry.Pos
import scalafx.geometry.Insets
import minirpg.model.world._

class VitalsGraph(actor : Actor) extends GridPane
  with Subscriber[ActorEvent, Actor] with Initializable {
  hgap = 8;
  vgap = 2;
  padding = Insets(2, 4, 2, 4);
  background = FXUtils.DefaultBackground;
  border = FXUtils.DefaultBorder;
  
  val vitalNames = actor.vitals.keySet.toVector
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    children.clear;
    var i = 0;
    for (v <- vitalNames) {
      val vital = actor.vitals(v);
      val level = vital._1;
      val maxLevel = vital._2;
      val perc = 100 * level / maxLevel;
      val color = VitalsGraph.getPercColor(perc);
      
      val label = new Text(v);
      val barStack = new StackPane {
        val bgRect = Rectangle(256, 10, Color.DIMGRAY);
        bgRect.stroke = Color.BLACK;
        val fgRect = Rectangle(256 * level / maxLevel, 10, color);
        fgRect.stroke = Color.BLACK;
        content.addAll(bgRect, fgRect);
        alignment = Pos.CENTER_LEFT;
      };
      add(label, 0, i);
      add(barStack, 1, i);
      i += 1;
    }
  }
  
  def init = {
    actor.subscribe(this, e => e.event == ActorEvent.VITALS_CHANGED);
    notify(null, null);
  }
  
}

object VitalsGraph {
  
  val percColors =
    Vector(
      (80, Color.GREEN),
      (60, Color.LIMEGREEN),
      (40, Color.YELLOW),
      (20, Color.ORANGE),
      (0, Color.RED)
    );
  
  def getPercColor(perc : Int) : Color = {
    for (c <- percColors if perc > c._1) return c._2;
    return null;
  }
}