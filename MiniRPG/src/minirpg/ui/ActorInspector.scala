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
  center = skillDisplay;
  
  // Equipment display.
  val equipmentDisplay = new Text("Equipment display");
  
  // Power display, with display of unavailable powers and why they're unavailable.
  val powerDisplay = new Text("Power display");
  
  // Bottom tabs controlling what is displayed in the center.
  val tabButtons = Vector(
      new Button("Skills") {
        onAction = handle { center = skillDisplay };
      },
      new Button("Equipment") {
        onAction = handle { center = equipmentDisplay };
      },
      new Button("Powers") {
        onAction = handle { center = powerDisplay };
      }).map(b => (b.text(), b)).toMap;
  
  val tabBox = new HBox {
    for (b <- tabButtons.values) {
      b.prefWidth <== width / tabButtons.size;
      children.add(b);
    }
  };
  bottom = tabBox;
  

}