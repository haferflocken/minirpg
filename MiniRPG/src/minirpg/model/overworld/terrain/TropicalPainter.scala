package minirpg.model.overworld.terrain

import scalafx.scene.paint.Paint
import scalafx.scene.paint.Color
import scala.Vector

object TropicalPainter extends TerrainPainter {
  
  val waterColors = Vector[(Double, Color)](
      (-1.0, Color.rgb(55, 22, 154)),
      (-0.8, Color.rgb(19, 34, 164)),
      (-0.6, Color.rgb(45, 80, 247)),
      (-0.2, Color.rgb(82, 165, 248)),
      (0.0,  Color.rgb(135, 206, 250)));
  
  def paintForLand(height : Double, gradient : (Double, Double)) : Paint = {
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
    
    // Valley:		Slope: [0, 50)		Height: [0%, 80%)		Water proximity: No
    if (slope < 80 && height > 0.05)
      return Color.LIMEGREEN;
    
    // Hills:		Slope: [50, 220)	Height: [0%, 80%)		Water proximity: No
    if (slope >= 80 && height > 0.05)
      return Color.LAWNGREEN;
    
    // Shore:	Slope: [0, 220)		Height: [0%, 25%)			Water proximity: Yes
    if (height < 0.25)
      return Color.KHAKI;
    
    // Errors. Shouldn't ever get here, so make sure it's noticeable.
    println(s"Height: $height, Slope: $slope");
    return Color.RED;
  }

  def paintForWater(depth : Double, gradient : (Double, Double)) : Paint = {
    for ((d, c) <- waterColors if depth <= d)
      return c;
    return Color.PINK;
  };
  
}