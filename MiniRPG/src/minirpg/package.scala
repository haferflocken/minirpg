
import minirpg.model._
import minirpg.model.world._
import minirpg.gear._
import minirpg.util._
import minirpg.entities._
import minirpg.entities.actors._
import javafx.scene.input.KeyCode

package object minirpg {
  
  val TENTOTHE9 : Long = 1000000000;
  val TENTOTHE11 : Long = TENTOTHE9 * 100;
  
  val gearMap =
    Vector[Gear](
        Shortsword,
        CyclopsGoggles, CyclopsJacket, CyclopsPants, CyclopsGloves, CyclopsBoots,
        FrogHelmet, FrogCuirass, FrogGreaves, FrogGauntlets, FrogBoots,
        CyanOrb, MagentaOrb, YellowOrb, BlackOrb,
        ConduitBlaster, ConduitRifle, ConduitMineLauncher
    ).map((e) => (e.name, e)).toMap[String, Gear];
  
  val builderMap =
    Vector[Builder[_]](
      GearEntityBuilder,
      HumanBuilder
    ).map((e) => (e.buildName, e)).toMap[String, Builder[_]];
  
  val numsToDigitKeys = 
    Map[Int, KeyCode](
        0 -> KeyCode.DIGIT0, 
        1 -> KeyCode.DIGIT1,
        2 -> KeyCode.DIGIT2,
        3 -> KeyCode.DIGIT3,
        4 -> KeyCode.DIGIT4,
        5 -> KeyCode.DIGIT5,
        6 -> KeyCode.DIGIT6,
        7 -> KeyCode.DIGIT7,
        8 -> KeyCode.DIGIT8,
        9 -> KeyCode.DIGIT9);
  
  val numsToFKeys = 
    Map[Int, KeyCode](
        1 -> KeyCode.F1,
        2 -> KeyCode.F2,
        3 -> KeyCode.F3,
        4 -> KeyCode.F4,
        5 -> KeyCode.F5,
        6 -> KeyCode.F6,
        7 -> KeyCode.F7,
        8 -> KeyCode.F8,
        9 -> KeyCode.F9,
        10 -> KeyCode.F10,
        11 -> KeyCode.F11,
        12 -> KeyCode.F12,
        13 -> KeyCode.F13,
        14 -> KeyCode.F14,
        15 -> KeyCode.F15,
        16 -> KeyCode.F16,
        17 -> KeyCode.F17,
        18 -> KeyCode.F18,
        19 -> KeyCode.F19,
        20 -> KeyCode.F20,
        21 -> KeyCode.F21,
        22 -> KeyCode.F22,
        23 -> KeyCode.F23,
        24 -> KeyCode.F24);
  
  /*
   * Global variables.
   */
  var global_debugPaths : Boolean = true;
  // TODO add more over time
  
  /*
   * Implicit class definitions.
   */
  
  implicit class OpsForString(val str : java.lang.String) {
    import scala.util.control.Exception.catching
    
    def toIntOpt = catching(classOf[NumberFormatException]).opt(str.toInt);
  }
  
  implicit class OpsForSeq(val seq : Seq[_]) {
    
    def mkEvenString(open : String = "", sep : String, close : String = "") : String = {
      val strs = for (i <- seq) yield i.toString;
      val maxLength = strs.foldLeft(0)((z : Int, s : String) => z max s.length);
      val padded = for (s <- strs) yield s.reverse.padTo(maxLength, ' ').reverse;
      return padded.mkString(open, sep, close);
    }
  }
  
  implicit class OpsFor2DIntSeq(val seq : Seq[Seq[Int]]) {
    
    def toPrettyString : String = {
      val subs = for (e <- seq) yield e.mkEvenString("  {", ", ", "}");
      return subs.mkString("{\n", "\n", "\n}");
    }
    
    def hasContent = seq != null && seq.length > 0 && seq(0) != null && seq(0).length > 0;
  }

  implicit class OpsFor2DSeq(val seq : Seq[Seq[_]]) {
    
    def toPrettyString : String = {
      val subs = for (e <- seq) yield e.mkEvenString("  {", ", ", "}");
      return subs.mkString("{\n", "\n", "\n}");
    }
    
    def hasContent = seq != null && seq.length > 0 && seq(0) != null && seq(0).length > 0;
  }
  
}