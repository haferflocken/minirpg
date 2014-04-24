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
import scala.collection.mutable.ArrayBuffer
import scalafx.scene.control.MenuItem
import scalafx.scene.control.ContextMenu
import scalafx.scene.control.Menu
import scalafx.geometry.Side
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.BorderPane
import minirpg.ui.scenes.WorldScene
import minirpg.util.Tickable

object MiniRPGApp extends JFXApp {
  
  val ticker = new DeltaTicker(tick);
  
  val world = WorldLoader.loadJsonFile("res\\ex\\world1.json");
  val worldScene : Scene with Tickable with Initializable = new WorldScene(world);
  
  println(world);
  
  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = 800;
    height = 600;
    resizable = false;
    scene = worldScene;
  }
  worldScene.init;
  ticker.start;
  
  private def tick(delta : Long) : Unit = {
    worldScene tick delta;
  }
  
}