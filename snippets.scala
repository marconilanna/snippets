/*
 * Bidirectional conversions between corresponding Scala and Java collections.
 * For a list of supported conversions, see:
 * http://www.scala-lang.org/api/current/scala/collection/JavaConversions$.html
 * http://www.scala-lang.org/api/current/scala/collection/JavaConverters$.html
 */

// Implicit, automatic conversions
import scala.collection.JavaConversions._

// asScala, asJava, asJavaCollection, asJavaEnumeration, asJavaDictionary
import scala.collection.JavaConverters._



/*
 * Mutable collections.
 * Do not import a mutable collection directly. Import the "mutable" package
 * instead and use the "mutable." prefix to denote mutability explicitly.
 */

import scala.collection.mutable
val set = mutable.Set(1, 2, 3)

// Counter-example:
import scala.collection.mutable.Set
val set = Set(1, 2, 3) // ambiguous for the inattentive reader



/*
 * Notable annotations
 */

// Automatic Java get and set methods
import scala.reflect.BeanProperty

// Warns when tail-recursion optimization is not possible
@tailrec

// Suppress exhaustivity checking for patterns
(e: @unchecked) match { ... }



/*
 * Main method
 */

object Echo {
	def main(args: Array[String]) {
		for (arg <- args) println(arg)
	}
}



/*
 * Collection initialization
 */

val map = Map("one" -> 1, "two" -> 2, "three" ->3)

val list = List(1, 2, 3)
val list = Seq(1, 2, 3) // same as above
val list = 1 :: 2 :: 3 :: Nil // same as above



/*
 * Flexible casting.
 * The following is semantically equivalent to <code>asInstanceOf[Any]</code>, but more flexible. For
 * instance, it is possible to use different branches to perform multiple conditional casts at the same time
 * for various types, perform conversions, and fallback or return <code>null</code> or <code>None</code>
 * instead of throwing an exception, etc.
 */

e match {
	case a: Any => a
	case _ => throw new ClassCastException
}



/*
 * Get current system time
 */

import System.{currentTimeMillis => now}



/*
 * Regular expression extraction
 */

val regex = "(.)(.)(.)".r  // creates a scala.util.matching.Regex object
val regex(a, b, c) = "xyz" // matches and extracts regex against "xyz"
assert((a, b, c) == ("x", "y", "z"))
