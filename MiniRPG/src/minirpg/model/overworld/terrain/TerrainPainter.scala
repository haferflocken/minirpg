package minirpg.model.overworld.terrain

import scalafx.scene.paint.Paint

trait TerrainPainter {
  
  /**
   * Get the paint for a point above sea level in the terrain.
   * @param height		must be normalized to the range [0, 1]
   * 					where 0 is sea level and 1 is the highest elevation above sea level.
   * @param gradient	the gradient (x', y') of the terrain, in non-normalized units. 
   */
  def paintForLand(height : Double, gradient : (Double, Double)) : Paint;
  
  /**
   * Get the paint for a point below sea level in the terrain.
   * @param depth		must be normalized to the range [-1, 0]
   * 					where 0 is sea level and -1 is the lowest depth below sea level.
   * @param gradient	the gradient (x', y') of the terrain, in non-normalized units.
   */
  def paintForWater(depth : Double, gradient : (Double, Double)) : Paint;

}
