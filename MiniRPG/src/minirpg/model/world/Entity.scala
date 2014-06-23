package minirpg.model.world

import scalafx.scene.Node
import minirpg.model._
import minirpg.model.world._
import minirpg.ui.MiniRPGApp

trait Entity {
  
  val id : String;
  val name : String;
  val description : String = null;
  val useable : Boolean = false;
  val node : Node;
  val nodeWidth : Int;
  val nodeHeight : Int;
  
  private var _x : Int = 0;
  private var _y : Int = 0;
  private var _world : World = null;
  
  def x_=(a : Int) : Unit = {
    _x = a;
    if (_world != null && node != null) {
      val tileGrid = _world.tileGrid;
      node.layoutX = _x * tileGrid.tileWidth + nodeOffsetX;
    }
  };
  
  def y_=(a : Int) : Unit = {
    _y = a;
    if (_world != null && node != null) {
      val tileGrid = _world.tileGrid;
      node.layoutY = _y * tileGrid.tileHeight + nodeOffsetY;
    }
  };
  
  def world_=(w : World) : Unit = {
    _world = w;
    if (_world != null && node != null) {
      val tileGrid = _world.tileGrid;
      node.layoutX = _x * tileGrid.tileWidth + tileGrid.tileWidth / 2 - node.minWidth(tileGrid.pixelWidth) / 2;
      node.layoutY = _y * tileGrid.tileHeight + tileGrid.tileHeight / 2 - node.minHeight(tileGrid.pixelHeight) / 2;
    }
  };
  
  def x = _x;
  def y = _y;
  def world = _world;
  
  def nodeOffsetX = world.tileGrid.tileWidth / 2 - nodeWidth / 2;
  def nodeOffsetY = world.tileGrid.tileHeight / 2 - nodeHeight / 2;
  
  def tick(delta : Long) : Unit = {};
  
  def beUsedBy(user : Entity) : Unit = {};
  
  override def equals(o : Any) : Boolean = {
    if (!o.isInstanceOf[Entity])
      return false;
    return id.equals(o.asInstanceOf[Entity].id);
  };
  
  override def hashCode : Int = id.hashCode;

  override def toString() : String = s"$id ($x, $y)";
}

abstract class EntityBuilder[E <: Entity] extends Builder[Entity] {
  
  def extractName(args : Map[String, Any]) : String = extract[String]("name", args, null);
  
  def extractCoords(args : Map[String, Any]) : (Int, Int) = {
    val pX = extract[Double]("x", args, Double.MinValue);
    val pY = extract[Double]("y", args, Double.MinValue);
    if (pX == Double.MinValue || pY == Double.MinValue) 
      return null;
    return (pX.toInt, pY.toInt);
  };
  
}