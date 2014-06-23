package minirpg.ui

import scalafx.scene.image.ImageView
import minirpg.model.Sprite
import minirpg.ui.animation.SpriteTransition
import scalafx.geometry.Rectangle2D

class SpriteView extends ImageView {

  private var _sprite : Sprite = null;
  private var _anim : SpriteTransition = null;
  
  def sprite = _sprite;
  
  def sprite_=(o : Sprite) : Unit = {
    if (_anim != null)
      _anim.stop;
    
    _sprite = o;
    image = _sprite.sheet;
    viewport = new Rectangle2D(0, 0, _sprite.frameWidth, _sprite.frameHeight);
    _anim = new SpriteTransition(this, _sprite.duration, _sprite.frameWidth, _sprite.frameHeight, _sprite.columns, _sprite.rows);
    anim.play();
  };
  
  def anim = _anim;
  
}

object SpriteView {
  
  def apply(_sprite : Sprite) = new SpriteView {
    sprite = _sprite;
  };
  
}