package minirpg.model.overworld

import scalafx.scene.paint.Paint
import scalafx.scene.paint.Color

object TropicalPainter extends TerrainPainter {

  val waterPaint : Paint = Color.LIGHTSKYBLUE;
  
  val mudPaint : Paint = Color.rgb(91, 39, 17);
  
  def paintFor(height : Double, gradient : (Double, Double), waterLevel : Double) : Paint = {
    val slope = Math.max(Math.abs(gradient._1), Math.abs(gradient._2));
    
    // Cliff:		Slope: [220, inf)	Height: Any				Water proximity: Any
    if (slope >= 220)
      return Color.GRAY;
    
    // Snow cap:	Slope: [0, 220)		Height: [80%, 100%]		Water proximity: Any
    if (height >= 0.75)
      return Color.SNOW;
    
    // Forest:		Slope: [50, 220) 	Height: [25%, 80%)		Water proximity: Any
    if (slope >= 80  && height >= 0.25)
      return Color.FORESTGREEN;
    
    val waterProximity = height - waterLevel;
    
    // Valley:		Slope: [0, 50)		Height: [0%, 80%)		Water proximity: No
    if (slope < 80 && waterProximity > 0.05)
      return Color.LIMEGREEN;
    
    // Hills:		Slope: [50, 220)	Height: [0%, 80%)		Water proximity: No
    if (slope >= 80 && waterProximity > 0.05)
      return Color.LAWNGREEN;
    
    // Shore:	Slope: [0, 220)		Height: [0%, 25%)		Water proximity: Yes
    if (height < 0.25)
      return Color.KHAKI;
    
    println("Height: " + height + ", Slope: " + slope + ", Water proximity: " + waterProximity);
    return Color.RED;
  }

  
}