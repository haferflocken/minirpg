package minirpg.ui

import minirpg.model._
import scalafx.scene.chart.BarChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.CategoryAxis
import scala.collection.mutable.Subscriber
import scalafx.scene.chart.XYChart
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets

class VitalsGraph(actor : Actor) extends BarChart(new NumberAxis, new CategoryAxis) with Subscriber[ActorEvent, Actor] {
  
  actor.subscribe(this, e => e.event == ActorEvent.VITALS_CHANGED);
  
  val vitalNames = actor.vitals.keySet.toVector;
  val vitalSeries = new XYChart.Series(XYChart.Series[Number, String](ObservableBuffer(actor.vitals.map(v => XYChart.Data[Number, String](v._2, v._1)).toSeq)));
  data = vitalSeries;
  YAxis.animated = false;
  legendVisible = false;
  categoryGap = 4;
  barGap = 4;
  maxWidth = 256;
  maxHeight = 64;
  
  notify(null, null);
  
  def notify(pub : Actor, event : ActorEvent) : Unit = {
    val itr = vitalSeries.data.get.iterator;
    while (itr hasNext) {
      val d = itr.next;
      d.setXValue(actor.vitals(d.getYValue));
    }
  }
  
}