/*
 * document.ready short notation
 */

$(function () {
})



/*
 * jQuery $ function is a constructor, and a very expensive one.
 * Do not abuse it. Invoke it once an assign it to a variable.
 */

var p = $('p')
p.doSomething()
p.doSomethingElse()
p.doYetAnotherThing()

// Counter-example: this is very inefficient!
$('p').doSomething()
$('p').doSomethingElse()
$('p').doYetAnotherThing()
