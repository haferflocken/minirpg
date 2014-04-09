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

object MiniRPGApp extends JFXApp {
  
  val player = new Human("Player") {
    x = 2;
    y = 2;
  };
  val tileGrid = new TileGrid(
      Array(
          Array(1, 1, 1, 1, 1),
          Array(1, 0, 0, 0, 1),
          Array(1, 0, 0, 0, 1),
          Array(1, 0, 0, 0, 1),
          Array(1, 1, 1, 1, 1)),
      Map[Int, Image](0 -> null, 1 -> new Image("file:res\\greenSquare.png")),
      32);
  val world = new World(tileGrid, Vector(player));

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