package minirpg.ui.overworld

import scalafx.Includes._
import scalafx.scene.image.ImageView
import minirpg.model.overworld.Landmark
import scalafx.scene.control.Tooltip
import scalafx.scene.input.MouseEvent
import minirpg.loaders.WorldLoader
import minirpg.ui.MiniRPGApp
import minirpg.ui.scenes.WorldScene

class LandmarkDisplay(val landmark : Landmark, val destroyed : Boolean) extends ImageView {

  if (destroyed) {
    image = Landmark.DestroyedImage;
    Tooltip.install(this, landmark.name + " (Destroyed)");
  }
  else {
    image = Landmark.Image;
    Tooltip.install(this, landmark.name);
    onMouseClicked = (me : MouseEvent) => {
      println(landmark.toString);
      val world = WorldLoader.loadJsonFile(landmark.worldPath);
      MiniRPGApp.scene = new WorldScene(world, landmark.portalIndex);
    };
  }
  
}