
package object minirpg {
  
  implicit class OpsForString(val str : java.lang.String) {
    import scala.util.control.Exception.catching
    
    def toIntOpt = catching(classOf[NumberFormatException]).opt(str.toInt);
  }
  
  implicit class OpsFor2DIntArray(val arr : Array[Array[Int]]) {
    
    def toPrettyString : String = {
      val subs = for (e <- arr) yield e.mkString("  {", ", ", "}");
      return subs.mkString("{\n", "\n", "\n}");
    }
    
    def hasContent : Boolean = {
      return arr != null && arr.length > 0 && arr(0) != null && arr(0).length > 0;
    }
  }
  
  implicit class OpsFor2DArray(val arr : Array[Array[_]]) {
    
    def toPrettyString : String = {
      val subs = for (e <- arr) yield e.mkString("  {", ", ", "}");
      return subs.mkString("{\n", "\n", "\n}");
    }
    
    def hasContent : Boolean = {
      return arr != null && arr.length > 0 && arr(0) != null && arr(0).length > 0;
    }
  }
  
}