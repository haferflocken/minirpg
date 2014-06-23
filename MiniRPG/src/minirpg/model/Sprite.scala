package minirpg.model

import scalafx.scene.image.ImageView
import scalafx.scene.image.Image
import scalafx.util.Duration

class Sprite(
    val sheet : Image, 
    val duration : Duration,
    val frameWidth : Int, 
    val frameHeight : Int,
    val columns : Int, 
    val rows : Int) {
  
  def this(sheet : Image) = this(sheet, null, sheet.width().toInt, sheet.height().toInt, 1, 1);
  
  val isAnimated = columns > 1 || rows > 1;
  
}