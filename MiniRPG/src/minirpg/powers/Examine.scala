package minirpg.powers

import minirpg.model._
import minirpg.model.world._
import minirpg.ui.MiniRPGApp
import minirpg.ui.ActorGUI
import scalafx.scene.image.Image

object Examine extends Power {
  
  val name = "Examine";
  val range = 1;
  val cooldown : Long = 0;
  val spriteId = null;
  val animDuration : Long = 0;
  
  def apply(user : Actor, targets : Vector[Entity], region : Region) = {
    val gui = ActorGUI.guis(user);
    for (t <- targets if t.description != null) {
      gui.showPopup(t.description , t.node.layoutX(), t.node.layoutY());
    }
  }
  
  def canBeUsedBy(user : Actor) = true;
  
  def mkRegion(cX : Int, cY : Int) = Region.tile(cX, cY);
  
  val uiImage = new Image("file:res/sprites/ui/powers/examine.png");

}