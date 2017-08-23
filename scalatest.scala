/*
 * sbt configuration
 */

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test

logBuffered in Test := false

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest
// D: show duration for each test
// I: print "reminders" of failed and canceled tests at the end of the summary
//    eliminates the need to scroll and search to find what tests failed or were canceled
// K: exclude canceled tests from reminder
, "-oDI"
// enforce chosen testing styles
, "-y", "org.scalatest.FreeSpec"
, "-y", "org.scalatest.AsyncFreeSpec"
// Periodic notification of slowpokes (tests that have been running longer than 30s)
, "-W", "30", "30"
)

/*
 * Base Spec class
 */

import org.mockito.Mockito.{RETURNS_SMART_NULLS => smartNulls}
import org.scalatest.{AsyncFreeSpec, DiagrammedAssertions, EitherValues, FreeSpec, OptionValues, TryValues}
import org.scalatest.mockito.MockitoSugar

import scala.reflect.ClassTag

/*
 * Mixed-in traits:
 *
 * FreeSpec and AsyncFreeSpec: nested tests inside text clauses denoted with the dash operator (-)
 * DiagrammedAssertions: show diagram of expression values when the assertion fails
 * EitherValues: `left.value` and `right.value` methods for `Either`
 * OptionValues: `value` method for `Option`
 * TryValues: `success` and `failure` methods for `Try`
 * MockitoSugar: syntax sugar for Mockito
 *
 * Also consider mixing:
 *
 * Inside: make assertions about nested object graphs using pattern matching
 * Inspectors: enable assertions to be made about collections
 * PartialFunctionValues: `valueAt` method for `PartialFunction`
 * PrivateMethodTester: testing of private methods
 * TableDrivenPropertyChecks: property checks against tables of data
 * WebBrowser: domain specific language for browser-based tests
 */
trait SpecLike
  extends DiagrammedAssertions
  with EitherValues
  with OptionValues
  with TryValues
  with MockitoSugar {
  override def mock[T <: AnyRef : ClassTag]: T = mock(smartNulls)
}

// general tests
abstract class Spec
  extends FreeSpec
  with SpecLike

// non-blocking asynchronous tests
abstract class AsyncSpec
  extends AsyncFreeSpec
  with SpecLike

/*
 * Sample test
 */

import org.mockito.Mockito.when

class SampleSpec extends Spec {
  class Context {
    // shared objects
    val expected = "forty two"

    // shared mocks
    val obj = mock[AnyRef]

    // common expectations
    when(obj.toString) thenReturn expected

    // helper functions

    // test setup
  }

  "Test:" - {
    "Sample test" in new Context {
      val result = obj.toString

      assert(!result.isEmpty)
      assert(result == expected)
    }

    "temporarily disabled test" ignore {
      assert(false)
      // To mark an entire suite of tests as ignored, annotate the test class with `@Ignore`
      // To prevent all tests from being discovered and reported as ignored use `@DoNotDiscover`
    }

    "not yet implemented" in (pending)

    // temporarily change failing test into pending test
    // test fails once problem is fixed, reminding you to revisit it
    "broken test" in pendingUntilFixed {
      ???
    }
  }
}

/*
 * Sample async test
 */

import scala.concurrent.Future

class SampleAsyncSpec extends AsyncSpec {
  class Context {
    // same as above
  }

  "Async Test:" - {
    "Sample async test" in {
      val ctx = new Context; import ctx._

      val future = Future.successful(obj.toString)

      future map { result =>
        assert(!result.isEmpty)
        assert(result == expected)
      }
    }

    "Multiple futures test" in {
      val fa = Future.successful(Random.nextInt(6))
      val fb = Future.successful(Random.nextBoolean)

      for {
        a <- fa
        b <- fb
      } yield {
        assert(a < 6)
        assert(b || !b) // that is the question

        note(s"a was $a, b was $b")
        // async tests must end with assertion; use `succeed` if they don't
        succeed
      }
      // but be careful to not put it here, outside the future mapping
    }
  }
}

/*
 * Assertions
 */

// general assertions
assert(actual == expected, "optional message")

// differentiate expected from actual values
assertResult(expected, "optional message") {
  actual
}

// ensure code throws expected exception
assertThrows[ArithmeticException] {
  1 / 0
}

// capture and inspect exception
val ex = intercept[ArithmeticException] {
  1 / 0
}
assert(ex.getMessage contains "by zero")

// ensure future fails with expected exception
recoverToSucceededIf[Exception] { // AsyncSpec only
  future
}

// capture and inspect future exception
recoverToExceptionIf[Exception] { // AsyncSpec only
  future
} map { ex =>
  assert(ex.getMessage.nonEmpty)
}

// ensure a piece of code compiles (parses and type checks)
assertCompiles("val a = 1")

// ensure a piece of code does not compile
assertDoesNotCompile("val a: String = 1")

// ensure code does not compile due to type error (as opposed to syntax error)
assertTypeError("val a: String = 1")
assertTypeError("val a: 1") // Syntax error, assertion fails

// force failure
fail("optional message")

// conditionally cancel test (e.g, if a required resource is unavailable)
assume(database.isAvailable, "optional message")

// force cancellation
cancel("optional message")

/*
 * Fixture context: recommended when fixtures don't need to be cleaned up afterwards
 */

class SampleSpec extends Spec {
  // fixture contexts
  class Context {
    // shared objects

    // shared mocks

    // common expectations

    // helper functions

    // test setup
  }

  class AugmentedContext extends Context {
    // ...
  }

  trait Mixin {
    // ...
  }

  "Test:" - {
    "Simple test" in new Context {
      // ...
    }

    "Augmented mixin test" in new AugmentedContext with Mixin {
      // ...
    }
  }
}

/*
 * Fixture method: recommended for async tests, since it is not possible to use fixture contexts
 */

class SampleAsyncSpec extends AsyncSpec {
  // fixture contexts
  class Context {
    // ...
  }

  class AugmentedContext extends Context {
    // ...
  }

  trait Mixin {
    // ...
  }

  def context = new Context
  def augmentedContext = new AugmentedContext
  def mixinContext = new Context with Mixin
  def mixinAugmentedContext = new AugmentedContext with Mixin

  "Test:" - {
    "Simple test" in {
      val ctx = context; import ctx._
      // ...
    }

    "Augmented mixin test" in {
      val ctx = mixinAugmentedContext; import ctx._
      // ...
    }
  }
}

/*
 * Loan pattern: recommended when fixtures must be cleaned up afterwards
 */

class SampleSpec extends Spec {
  def withDatabase(testCode: Db => Any) {
    // create fixture
    val db = new Db

    try {
      // setup
      db.connect
      // loan
      testCode(db)
    } finally {
      // clean up
      db.close
    }
  }

  "Test:" - {
    "Simple test" in withDatabase { db =>
      assert(db.isAvailable)
    }
  }
}

class SampleAsyncSpec extends AsyncSpec {
  def withDatabase(testCode: Future[Db] => Future[Assertion]) = {
    // create fixture
    val db = Future { new Db }

    complete {
      // setup
      val openDb = db map (_.connect)
      // loan
      testCode(openDb)
    } lastly {
      // clean up
      db map (_.close)
    }
  }

  "Test:" - {
    "Simple test" in withDatabase { fdb =>
      fdb map { db =>
        assert(db.isAvailable)
      }
    }
  }
}

/*
 * Shared tests
 */

trait Behaviors { this: FreeSpec =>
  def behaviorA(myObj: => MyClass) {
    "test A1" in {
      val obj = myObj
      // ...
    }

    "test A2" in {
      // ...
    }
  }

  def behaviorB(obj: => MyClass) {
    "test B1" in {
      // ...
    }

    "test B2" in {
      // ...
    }
  }
}

class SampleSpec extends Spec with Behaviors {
  def myObj = new MyClass

  "Simple test" - {
    "should" - {
      behave like behaviorA(myObj)
      behave like behaviorB(myObj)
    }
  }
}

/*
 * Collections: mix in trait `Inspectors`
 */

val seq = Seq(1, 2, 3, 4, 5)

// holds true for all elements (reports only the first failing element)
forAll (seq) { n =>
  assert(n % 2 == 1)
}

// holds true for every element (reports all failing elements)
forEvery (seq) { n =>
  assert(n % 2 == 1)
}

// holds true for exactly the specified number of elements
forExactly (3, seq) { n =>
  assert(n % 2 == 1)
}

// holds true for at least the specified number of elements
forAtLeast (2, seq) { n =>
  assert(n % 2 == 1)
}

// holds true for at most the specified number of elements
forAtMost (2, seq) { n =>
  assert(n % 2 == 0)
}

// holds true for between the specified minimum and maximum number of elements, inclusive
forBetween (1, 3, seq) { n =>
  assert(n % 2 == 0)
}

// nesting
val magicSquare = Seq(
  Seq(4, 9, 2)
, Seq(3, 5, 7)
, Seq(8, 1, 6)
)

forAll (magicSquare) { row =>
  assert(row.sum == 15)

  forAtLeast (1, row) { n =>
    assert(n % 2 == 1)
  }
}

/*
 * Private methods: mix in trait `PrivateMethodTester`
 */

case class Say(s: String) {
  private def say = s + "!"
}

// type parameter must match result type of private method
// symbol must match name of private method to invoke
val say = PrivateMethod[String]('say)

val obj = Say("hi")

val result = obj invokePrivate say("hi")

assert(result == "hi!")

/*
 * Notifications
 */

// note and alert are for notifications:
// sent immediately so users can track progress of long-running tests
note("status notification")
alert("warnings and potential problems")

// info is for text that is part of the specification:
// sent after test completion in a color determined by test outcome
info("info")
