package minirpg.ui

import javafx.scene.paint.Paint
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.BackgroundFill
import javafx.geometry.Insets
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCode
import scalafx.scene.layout.Background
import scalafx.scene.layout.Border
import scalafx.scene.paint.Color


object FXUtils {
  
  val DefaultBackground = makeSFXBackground(Color.LIGHTGRAY);
  val DefaultActionBackground = makeSFXBackground(Color.PALEGREEN);
  
  val DefaultBorder = makeSFXBorder(Color.BLACK);
  val DefaultActionBorder = makeSFXBorder(Color.BLACK);
  
  def makeSFXBackground(paint : Paint, corners : CornerRadii = CornerRadii.EMPTY, insets : Insets = Insets.EMPTY)
    = new Background(makeJFXBackground(paint, corners, insets));
  
  def makeJFXBackground(paint : Paint, corners : CornerRadii = CornerRadii.EMPTY, insets : Insets = Insets.EMPTY)
    = new javafx.scene.layout.Background(new BackgroundFill(paint, corners, insets));
  
  def makeSFXBorder(paint : Paint, strokeStyle : BorderStrokeStyle = BorderStrokeStyle.SOLID, corners : CornerRadii = CornerRadii.EMPTY, strokeWidths : BorderWidths = BorderStroke.THIN)
    = new Border(makeJFXBorder(paint, strokeStyle, corners, strokeWidths));
  
  def makeJFXBorder(paint : Paint, strokeStyle : BorderStrokeStyle = BorderStrokeStyle.SOLID, corners : CornerRadii = CornerRadii.EMPTY, strokeWidths : BorderWidths = BorderStroke.THIN)
    = new javafx.scene.layout.Border(new BorderStroke(paint, strokeStyle, corners, strokeWidths));
  
  def makeAccelerator(keyCode : KeyCode, action : Function0[Any]) : (KeyCodeCombination, Runnable) = 
    (new KeyCodeCombination(keyCode), new Runnable { override def run = action() });

}