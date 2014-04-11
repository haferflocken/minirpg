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

import minirpg.actors._
import minirpg.model._
import minirpg.loaders.WorldLoader

object MiniRPGApp extends JFXApp {
  
  val world = WorldLoader.loadJsonFile("res\\ex\\world1.json"); //new World("Test", tileGrid, Vector(player));
  println(world);

  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    scene = new Scene {
      fill = Color.BLACK;
      content = world.nodes;
    };
  }
  
}