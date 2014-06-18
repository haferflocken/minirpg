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
import minirpg.model.overworld._
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
import minirpg.ui.scenes._
import minirpg.util.Tickable

object MiniRPGApp extends JFXApp {
  
  val (width, height) = (800, 600);
  val ticker = new DeltaTicker(tick);
  private var _scene : Scene with Tickable with Initializable = new NewGameSetupScene
  
  var overworld : Overworld = null;
  
  stage = new JFXApp.PrimaryStage {
    title = "MiniRPG";
    width = MiniRPGApp.width;
    height = MiniRPGApp.height;
    resizable = true;
    scene = _scene;
  }
  _scene.init;
  ticker.start;
  
  def scene_=(s : Scene with Tickable with Initializable) : Unit = {
    _scene = s;
    stage.scene = s;
    s.init;
  }
  
  def scene = _scene;
  
  private def tick(delta : Long) : Unit = {
    _scene tick delta;
  }
  
}