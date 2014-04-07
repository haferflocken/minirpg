package minirpg.actors

import minirpg.model._
import minirpg.powers._

class Human(name : String) extends Actor(
    name, Array("Head", "Torso", "Legs", "Hands", "Feet", "Main Hand", "Off Hand"), List(Move)) {
  
  val node = null;
}