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

object MiniRPGApp extends JFXApp {
  
  val world = WorldLoader.loadJsonFile("res\\ex\\world1.json");
  val player = world.getEntityById("player").asInstanceOf[Actor];
  
  println(world);
  println("NavMap: ");
  println(world.tileGrid.navMap.mkString("\n"));
  
  player.setMoveTarget(2, 2);
  println(player.path);
  
  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    scene = new Scene {
      fill = Color.BLACK;
      content = world.nodes ++ world.debugPathNodes;
    };
  }
  
}