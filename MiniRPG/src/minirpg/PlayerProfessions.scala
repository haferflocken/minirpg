package minirpg

import minirpg.model.world.Gear
import minirpg.gear.Shortsword

object PlayerProfessions {
  
  sealed abstract class Profession(val name : String, val gear : Vector[Gear]);
  
  object StreetThug extends Profession("Street Thug", Vector());
  object RetiredMercenary extends Profession("Retired Mercenary", Vector(Shortsword));
  object Guard extends Profession("Guard", Vector());
  
  val all = Vector(StreetThug, RetiredMercenary, Guard);

}