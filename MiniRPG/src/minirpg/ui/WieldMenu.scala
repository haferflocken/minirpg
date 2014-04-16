package minirpg.ui

import scalafx.scene.Group
import minirpg.model.Gear
import scalafx.scene.Node
import scalafx.scene.chart.PieChart
import scalafx.collections.ObservableBuffer
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import minirpg.model.Actor
import minirpg.gearMap

class WieldMenu(val player : Actor) extends Group {
  
  val gearPie : PieChart = new PieChart() {
    labelsVisible = false;
    legendVisible = true;
    labelLineLength = 10;
  };
  refresh;
  children.add(gearPie);
  
  def refresh : Unit = {
    gearPie.data = toData(player.equipped.filter(_.wieldSlots != null));
    for (d <- new ObservableBuffer(gearPie.data.get())) {
      val wieldGear = gearMap(d.getName);
      
      if (!player.isWielding(wieldGear)) {
        d.getNode.setOpacity(0.5);
        d.getNode.addEventHandler(MouseEvent.MOUSE_PRESSED,
            new EventHandler[MouseEvent] {
              override def handle(me : MouseEvent) : Unit = player.wield(wieldGear);
            });
      }
      else {
        d.getNode.addEventHandler(MouseEvent.MOUSE_PRESSED,
            new EventHandler[MouseEvent] {
              override def handle(me : MouseEvent) : Unit = player.unwield(wieldGear);
            });
      }
    }
  }
  
  def setMaxWidth(w : Double) = gearPie.setMaxWidth(w);
  def setMaxHeight(h : Double) = gearPie.setMaxHeight(h);
  
  private def toData(g : Iterable[Gear]) : ObservableBuffer[javafx.scene.chart.PieChart.Data] =
    ObservableBuffer(g.map((g) => PieChart.Data(g.name, 1)).toSeq)
  
}