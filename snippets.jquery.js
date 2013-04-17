/*
 * document.ready short notation
 */

$(function () {
})



/*
 * jQuery $ function is a constructor, and a very expensive one.
 * Do not abuse it. Invoke it once and assign it to a variable.
 */

var foo = $('.foo')
foo.doSomething()
foo.doSomethingElse()
foo.doYetAnotherThing()

// Method chaining (whenever possible)
var foo = $('.foo')
foo.doSomething()
	.doSomethingElse()
	.doYetAnotherThing()

// Counter-example: this is very inefficient!
$('.foo').doSomething()
$('.foo').doSomethingElse()
$('.foo').doYetAnotherThing()
