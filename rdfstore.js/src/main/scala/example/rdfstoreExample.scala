package example

import scala.scalajs.js
import rdfstore.Store

/**
 * Created by hjs on 26/12/2013.
 */
object rdfstoreExample {
  val console = js.Dynamic.global.console

  def main(): Unit = {
    console.log("in main()")
    val store = Store.create { test _ }
    console.log("received something")
    console.log(store)
  }

  def test(store: Store) {
    console.log(s"in rdfstore.create, was successful")
    console.log(store)
  }
}
