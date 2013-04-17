/*
 * Main method
 */

// Java style
object Echo {
	def main(args: Array[String]) {
		for (arg <- args) println(arg)
	}
}

// Alternative
object Echo extends App {
	for (arg <- args) println(arg)
}



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
 * Bidirectional conversions between corresponding Scala and Java collections.
 * For a list of supported conversions, see:
 * http://www.scala-lang.org/api/current/scala/collection/JavaConversions$.html
 * http://www.scala-lang.org/api/current/scala/collection/JavaConverters$.html
 */

// Implicit, automatic conversions
import scala.collection.JavaConversions._

// asScala, asJava, asJavaCollection, asJavaEnumeration, asJavaDictionary
import scala.collection.JavaConverters._



/*
 * Transparently use Java collections as if they were Scala collections
 */

import scala.collection.JavaConversions.mapAsScalaMap
var map: scala.collection.mutable.Map[String, String] = new java.util.HashMap[String, String]
map += "foo" -> "bar"
assert(map("foo") == "bar")


/*
 * Mutable collections.
 * Do not import a mutable collection directly. Import the "mutable" package
 * instead and use the "mutable." prefix to denote mutability explicitly.
 */

import scala.collection.mutable
val set = mutable.Set(1, 2, 3)

// Counter-example
import scala.collection.mutable.Set
val set = Set(1, 2, 3) // Too risky for the inattentive reader



/*
 * Collection initialization
 */

val map = Map("one" -> 1, "two" -> 2, "three" -> 3)

val list = List(1, 2, 3)
val list = Seq(1, 2, 3) // Same as above
val list = 1 :: 2 :: 3 :: Nil // Same as above



/*
 * Initialization with pattern matching
 */

val tuple = ("foo", 1, 0)
val (x, y, z) = tuple
assert((x, y, z) == ("foo", 1, 0))

val option = Some("foo")
val Some(foo) = option
assert(foo == "foo")

case class Foo(x: String, y: Int)
val foo = Foo("foo", 1)
val Foo(a, b) = foo
assert((a, b) == ("foo", 1))

val list = List(1, 2, 3, 4, 5, 6)
val x :: xs = list
assert((x, xs) == (1, List(2, 3, 4, 5, 6)))
// Same as above
val List(x, xs@_*) = list
assert((x, xs) == (1, List(2, 3, 4, 5, 6)))
// Skipping elements
val _ :: a :: b :: _ :: xs = list
assert((a, b, xs) == (2, 3, List(5, 6)))
// Works with other collections, too
val vector = Vector(1, 2, 3, 4, 5, 6)
val Vector(_, a, b, _, xs@_*) = vector
assert((a, b, xs) == (2, 3, Vector(5, 6)))



/*
 * Seq to variable length argument list
 */

def foo(args: Int*) = args.foreach{println(_)}
foo(list:_*)



/*
 * Catching exceptions
 */

try {
	// ...
} catch {
	case e: Exception => // ...
	case _: Throwable => // Catches all
}

/*
 * Flexible casting.
 * The following is semantically equivalent to <code>asInstanceOf[Any]</code>, but more flexible. For
 * instance, it is possible to use different branches to perform multiple conditional casts at the same time
 * for various types, perform conversions, and fallback or return <code>null</code> or <code>None</code>
 * instead of throwing an exception, etc.
 */

e match {
	case a: AnyRef => a
	case _ => throw new ClassCastException
}



/*
 * Get current system time
 */

import System.{currentTimeMillis => now}



/*
 * Regular expression extraction
 */

val regex = """(.)(.)(.)""".r  // Creates a scala.util.matching.Regex object
val regex(a, b, c) = "xyz" // Matches and extracts regex against "xyz"
assert((a, b, c) == ("x", "y", "z"))

"xyz" match {
	case regex(a, b, c) => // Match found
	case _ => // No match
}



/*
 * Extractors
 */

object Twice {
	def unapply(x: Int) = if (x % 2 == 0) Some(x/2) else None
}
val Twice(i) = 20
assert(i == 10)
val Twice(j) = 15 // Throws MatchError

// Works with instances, too
class Foo(i: Int) {
	def unapply(x: Int) = if (x == i) Some(Some(x)) else Some(None)
}
val foo = new Foo(10)
val foo(a) = 10
assert(a == Some(10))
val foo(b) = 12
assert(b == None)
