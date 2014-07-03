package minirpg.ui

import minirpg.model.world._
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.Text
import scalafx.scene.layout.HBox
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.Includes._
import scalafx.scene.paint.Color
import scalafx.geometry.Pos
import scalafx.scene.text.TextAlignment
import minirpg.ui.scenes.WorldScene
import scalafx.scene.layout.StackPane
import scalafx.scene.control.TabPane
import scalafx.scene.control.Tab
import scalafx.geometry.Side

class ActorInspector(worldScene : WorldScene, actor : Actor) extends BorderPane {
  
  // Styling.
  background = FXUtils.DefaultBackground;
  border = FXUtils.DefaultBorder;
  
  // Top title bar: centered title and right aligned close button.
  val title = new Text(actor.name);
  val closeButton = new Button("[X]") {
    onAction = handle { worldScene.toggleGUIAndInspector; };
  };
  val titlePane = new StackPane {
    children.addAll(title, closeButton);
    title.alignmentInParent = Pos.TOP_CENTER;
    closeButton.alignmentInParent = Pos.TOP_RIGHT;
  };
  top = titlePane;
  
  // Skill display.
  val skillDisplay = new Text("Skill display");
  val skillTab = new Tab {
    text = "Skills";
    content = skillDisplay;
  };
  
  // Equipment display.
  val equipmentDisplay = new Text("Equipment display");
  val equipmentTab = new Tab {
    text = "Equipment";
    content = equipmentDisplay;
  };
  
  // Power display, with display of unavailable powers and why they're unavailable.
  val powerDisplay = new Text("Power display");
  val powerTab = new Tab {
    text = "Powers";
    content = powerDisplay;
  };
  
  // The tab pane which displays the different tabs.
  val tabPane = new TabPane {
    tabs = Seq(skillTab, equipmentTab, powerTab);
    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE;
    side = Side.BOTTOM;
  };
  center = tabPane;
  

}