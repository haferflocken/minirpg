package minirpg.actors

import minirpg.model._
import minirpg.powers._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color

class Human(name : String) extends Actor(
    name, Array("Head", "Torso", "Legs", "Hands", "Feet", "Main Hand", "Off Hand"), List(Move)) {
  
  val node = new Rectangle() {
    fill = Color.RED;
    width = 32;
    height = 32;
  };
}