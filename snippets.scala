/*
 * Main method
 */

// Java style
object Echo {
	def main(args: Array[String]): Unit = {
		args foreach { println _ }
	}
}

// Alternative
object Echo extends App {
	for (arg <- args) println(arg)
}



/*
 * Assertions.
 * Assertions are used to document and check design-by-contract style invariants in code. They can
 * be disabled at runtime with the `-Xdisable-assertions` command line option. For reference, see:
 * http://www.scala-lang.org/api/current/index.html#scala.Predef$
 */

assert(1 > 0)

// `assume` is intended for static code analysis tools. It is otherwise equivalent to `assert`.
assume(1 > 0)

// `require` is used to check pre-conditions, blaming the caller of a method for violating them.
// Unlike other assertions, `require` throws `IllegalArgumentException` instead of `AssertionError`
// and can never be disabled at runtime.
require(1 > 0)

// `ensuring` is used on a method's return value to check post-conditions.
def square(a: Int) = {a * a} ensuring(_ > 0)



/*
 * Exceptions
 */

import scala.util.control.NonFatal

try {
	// ...
} catch {
	case NonFatal(e) => // Recommended way to catch all
	case e: Exception => // ...
	case _: Throwable => // Not recommended
}



/*
 * Flexible casting.
 * The following is semantically equivalent to `asInstanceOf[Any]`, but more flexible.
 * For instance, it is possible to use different branches to perform multiple conditional casts at
 * the same time for various types, perform conversions, and fallback or return `None` or `null`
 * instead of throwing an exception, etc.
 */

e match {
	case a: AnyRef => a
	case _ => throw new ClassCastException
}



/*
 * Seq to variable length argument list
 */

def foo(args: Int*) = args.foreach{println(_)}
foo(list:_*)



/*
 * Notable annotations
 */

// Automatic Java get and set methods
import scala.beans.{BeanProperty, BooleanBeanProperty}

case class A(@BeanProperty var i: Int, @BooleanBeanProperty var b: Boolean)

val a = A(1, true)

a.setI(2)
a.getI

a.setB(false)
a.isB

// Warns when tail-recursion optimization is not possible
import scala.annotation.tailrec

@tailrec def f(i: Int, s: Int = 0): Int = if (i > 0) f(i - 1, s + i) else s

// Warns when a match compiles to conditional expressions instead of tableswitch or lookupswitch
import scala.annotation.switch

(e: @switch) match { ... }

// Suppress exhaustivity checking for pattern matching
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
import scala.collection.mutable
var map: mutable.Map[String, String] = new java.util.HashMap[String, String]
map += "foo" -> "bar"
assert(map("foo") == "bar")



/*
 * Mutable collections.
 * Do not import a mutable collection directly. Import the `mutable` package
 * instead and use the `mutable.` prefix to denote mutability explicitly.
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



/*
 * Enumerations.
 * Java enumerations, as described in Joshua Bloch's "Effective Java", are one of the language
 * most powerful features. In Scala, there are two commonly used alternatives to Java's `Enum`:
 * sealed case objects and the `Enumeration` trait. Unfortunately, none of them support all `Enum`
 * features: sealed case objects, for instance, cannot be enumerated (iterated over), and
 * `Enumeration` values cannot have fields or override methods. These examples shows how to
 * combine them to get a feature set equivalent to Java `Enum`.
 */

sealed trait Gender
case object Male extends Gender
case object Female extends Gender

object Season extends Enumeration {
	type Season = Value
	val Spring, Summer, Autumn, Winter = Value
}

object Suit extends Enumeration {
	type Suit = SuitVal

	implicit def toVal(v: Value) = v.asInstanceOf[SuitVal]

	case class SuitVal private[Suit] (symbol: Char) extends Val

	val Spades   = SuitVal('♠')
	val Hearts   = SuitVal('♥')
	val Diamonds = SuitVal('♦')
	val Clubs    = SuitVal('♣')
}

object Lang extends Enumeration {
	type Lang = LangVal

	implicit def toVal(v: Value) = v.asInstanceOf[LangVal]

	sealed abstract class LangVal extends Val {
		def greet(name: String): String
	}

	val English = new LangVal {
		def greet(name: String) = s"Welcome, $name."
	}

	val French = new LangVal {
		def greet(name: String) = s"Bienvenue, $name."
	}
}



/*
 * Get current system time
 */

import System.{currentTimeMillis => now}
import System.{nanoTime => now}
