
import minirpg.model._
import minirpg.gear._
import minirpg.entities.actors._

package object minirpg {
  
  val TENTOTHE9 : Long = 1000000000;
  val TENTOTHE11 : Long = TENTOTHE9 * 100;
  
  val gearMap =
    Vector[Gear](
        Shortsword,
        CyclopsGoggles, CyclopsJacket, CyclopsPants, CyclopsGloves, CyclopsBoots,
        FrogHelmet, FrogCuirass, FrogGreaves, FrogGauntlets, FrogBoots,
        RedOrb, YellowOrb, GreenOrb, BlueOrb, PurpleOrb
    ).map((e) => (e.name, e)).toMap[String, Gear];
  
  val builderMap =
    Vector[Builder[_]](
      GearEntityBuilder,
      HumanBuilder
    ).map((e) => (e.buildName, e)).toMap[String, Builder[_]];
  
  /*
   * Global variables.
   */
  var global_debugPaths : Boolean = false;
  // TODO add more over time
  
  /*
   * Implicit class definitions.
   */
  
  implicit class OpsForString(val str : java.lang.String) {
    import scala.util.control.Exception.catching
    
    def toIntOpt = catching(classOf[NumberFormatException]).opt(str.toInt);
  }
  
  implicit class OpsForArray(val arr : Array[_]) {
    
    def mkEvenString(open : String = "", sep : String, close : String = "") : String = {
      val strs = for (i <- arr) yield i.toString;
      val maxLength = strs.foldLeft(0)((z : Int, s : String) => z max s.length);
      val padded = for (s <- strs) yield s.reverse.padTo(maxLength, ' ').reverse;
      return padded.mkString(open, sep, close);
    }
  }
  
  implicit class OpsFor2DIntArray(val arr : Array[Array[Int]]) {
    
    def toPrettyString : String = {
      val subs = for (e <- arr) yield e.mkEvenString("  {", ", ", "}");
      return subs.mkString("{\n", "\n", "\n}");
    }
    
    def hasContent : Boolean = {
      return arr != null && arr.length > 0 && arr(0) != null && arr(0).length > 0;
    }
  }
  
  implicit class OpsFor2DArray(val arr : Array[Array[_]]) {
    
    def toPrettyString : String = {
      val subs = for (e <- arr) yield e.mkEvenString("  {", ", ", "}");
      return subs.mkString("{\n", "\n", "\n}");
    }
    
    def hasContent : Boolean = {
      return arr != null && arr.length > 0 && arr(0) != null && arr(0).length > 0;
    }
  }
  
}