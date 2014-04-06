package minirpg.ui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.Node

object MiniRPGApp extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    scene = new Scene {
      fill = Color.BLACK;
      content = Set();
    };
  }
  
}