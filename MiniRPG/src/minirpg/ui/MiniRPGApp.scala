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
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.MouseButton
import minirpg.util.DeltaTicker
import scalafx.scene.layout.Pane

object MiniRPGApp extends JFXApp {
  
  val ticker = new DeltaTicker(tick);
  
  val world = WorldLoader.loadJsonFile("res\\ex\\world1.json");
  val player = world.getEntitiesById("player")(0).asInstanceOf[Actor];
  
  val guiCanvas : Pane = new Pane;
  
  println(world);
  
  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    scene = new Scene {
      fill = Color.BLACK;
      content = Vector(world.canvas, world.debugCanvas, guiCanvas);
    };
    handleMouse(scene());
    handleKeys(scene());
  }
  ticker.start;
  
  private def tick(delta : Long) : Unit = {
    world.tick(delta);
  }
  
  private def handleMouse(scene : Scene) : Unit = {
    scene.onMouseClicked = (me : MouseEvent) => {
      val tileCoords = world.tileGrid.screenToTileCoords(me.x, me.y);
      // Left click is use.
      if (me.button == MouseButton.PRIMARY) {
        val useTargets = world.getEntitiesAt(tileCoords._1, tileCoords._2);
        if (useTargets.nonEmpty) {
          if ((tileCoords._1 - player.x).abs + (tileCoords._2 - player.y).abs <= 1 ) {
            val useTarget = useTargets(0);
            useTarget.beUsedBy(player);
          }
        }
      }
      // Right click is move.
      else if (me.button == MouseButton.SECONDARY) {
        player.setMoveTarget(tileCoords._1, tileCoords._2);
      }
    }
  }
  
  private def handleKeys(scene : Scene) : Unit = {
    scene.onKeyPressed = (ke : KeyEvent) => {
      // TODO
    }
  }
  
}