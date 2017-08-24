/*
 * Douglas Crockford Closure Object Pattern
 */

var Class = function () {
  this.method = function () {}
}
var object = new Class()



/*
 * Closure Pattern variation
 */

var myClass = function () {
  var privateMethod = function () {}
  var publicMethod = function () {}
  return {
    method: publicMethod
  }
}
var object = myClass()



/*
 * Prototypal Inheritance Object Pattern
 */

var Class = function () {}
Class.prototype = {
  method: function () {}
}
var object = new Class()
