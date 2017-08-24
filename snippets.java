/*
 * Double-checked locking, thread-safe lazy initialization
 */

class Foo {
  private volatile Bar bar = null;
  public Bar getBar() {
    if (bar == null) {
      synchronized (this) {
        if (bar == null) {
          bar = new Bar();
        }
      }
    }
    return bar;
  }
}
