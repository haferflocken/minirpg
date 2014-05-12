package minirpg.powers

import minirpg.model._
import minirpg.model.world._
import minirpg.ui.MiniRPGApp
import minirpg.ui.ActorGUI

object Examine extends Power {
  
  val name = "Examine";
  val range = 2;
  val cooldown : Long = 0;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    val gui = ActorGUI.guis(user);
    for (t <- targets if t.description != null) {
      gui.showPopup(t.description , t.node.layoutX(), t.node.layoutY());
    }
  }
  
  def canUse(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);

}