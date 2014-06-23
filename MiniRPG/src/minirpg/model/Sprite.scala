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
  
}