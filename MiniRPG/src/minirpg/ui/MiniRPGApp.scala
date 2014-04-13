package minirpg.ui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.Node
import scalafx.scene.image.Image
import minirpg.entities.actors._
import minirpg.model._
import minirpg.loaders.WorldLoader
import scalafx.scene.input.MouseEvent

object MiniRPGApp extends JFXApp {
  
  val world = WorldLoader.loadJsonFile("res\\ex\\world1.json");
  val player = world.getEntityById("player").asInstanceOf[Actor];
  
  println(world);
  
  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    scene = new Scene {
      fill = Color.BLACK;
    };
    setupContent(scene());
    handleMouse(scene());
  }
  
  private def setupContent(scene : Scene) : Unit = {
    scene.content = world.nodes ++ world.debugPathNodes;
  }
  
  private def handleMouse(scene : Scene) : Unit = {
    scene.onMouseClicked = (me : MouseEvent) => {
      val tileCoords = world.tileGrid.screenToTileCoords(me.x, me.y);
      player.setMoveTarget(tileCoords._1, tileCoords._2);
      setupContent(scene);
    }
  }
  
}