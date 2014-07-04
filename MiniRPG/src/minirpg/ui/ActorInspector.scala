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
import scalafx.scene.layout.GridPane
import scalafx.scene.control.ScrollPane
import scala.collection.mutable.Subscriber
import minirpg.model.Skills

class ActorInspector(worldScene : WorldScene, actor : Actor) extends BorderPane {
  import ActorInspector._
  
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
  val skillDisplay = new SkillDisplay(actor);
  val skillTab = new Tab {
    text = "Skills";
    content = skillDisplay;
  };
  
  // Equipment display.
  val equipmentDisplay = new EquipmentDisplay(actor);
  val equipmentTab = new Tab {
    text = "Equipment";
    content = equipmentDisplay;
  };
  
  // Power display, with display of unavailable powers and why they're unavailable.
  val powerDisplay = new PowerDisplay(actor);
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

  def enableUpdates : Unit = {
    skillDisplay.enableUpdates;
    equipmentDisplay.enableUpdates;
  };
  
  def disableUpdates : Unit = {
    skillDisplay.disableUpdates;
    equipmentDisplay.disableUpdates;
  };
}

object ActorInspector {
  
  // Displays skills in rows, sorted alphabetically.
  class SkillDisplay(actor : Actor) extends ScrollPane with Subscriber[Actor.Event, Actor] {
    val grid = new GridPane;
    private var updatesEnabled = false;
    
    content = grid;
    
    def enableUpdates : Unit = {
      if (updatesEnabled) return;
      updatesEnabled = true;
      import Actor.Event._
      actor.subscribe(this, _ match { case Equip(_) | Unequip(_) => true; case _ => false });
      this.notify(actor, null);
    };
    
    def disableUpdates : Unit = {
      if (!updatesEnabled) return;
      updatesEnabled = false;
      actor.removeSubscription(this);
    };
    
    def notify(pub : Actor, evt : Actor.Event) : Unit = {
      grid.children.clear;
      
      var i = 0;
      for (skill <- Skills.displayOrder) {
        val value = actor.skills(skill);
        grid.add(new Text(skill), 0, i);
        grid.add(new Text(String.valueOf(value)), 1, i);
        i += 1;
      }
    };
  }
  
  // Display equipment in groups of rows, each group representing a slot.
  class EquipmentDisplay(actor : Actor) extends ScrollPane with Subscriber[Actor.Event, Actor] {
    val grid = new GridPane;
    private var updatesEnabled = false;
    
    content = grid;
    
    def enableUpdates : Unit = {
      if (updatesEnabled) return;
      updatesEnabled = true;
      
      import Actor.Event._
      actor.subscribe(this, _ match { case Equip(_) | Unequip(_) | Wield(_) | Unwield(_) => true; case _ => false });
      this.notify(actor, null);
    };
    
    def disableUpdates : Unit = {
      if (!updatesEnabled) return;
      updatesEnabled = false;
      actor.removeSubscription(this);
    };
    
    def notify(pub : Actor, evt : Actor.Event) : Unit = {
      grid.children.clear;
      
      val sortedGear = actor.equipSlotOrder.map(slot => (slot, actor.equipSlotContents(slot)));
      
      var i = 0;
      for ((slot, gear) <- sortedGear) {
        grid.add(new Text(slot), 0, i);
        i += 1;
        for (g <- gear) {
          grid.add(new Text(g.name), 1, i);
          i += 1;
        }
      }
    };
  }
  
  // Display powers in rows, with available powers on top and unavailable powers on bottom.
  class PowerDisplay(actor : Actor) extends ScrollPane {
    val grid = new GridPane;
    
    def refresh : Unit = {
      grid.children.clear;
    };
  }
  
}