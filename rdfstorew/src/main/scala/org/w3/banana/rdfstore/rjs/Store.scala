package org.w3.banana.rdfstore.rjs

import org.w3.banana.rdfstorew.JSStore
import scala.concurrent.Promise
import scala.scalajs.js.annotation.JSName
import org.w3.banana.rdfstore.{rjs=>rjs}
import scala.scalajs.js


trait Store extends js.Object {

  type CallBack[A] = js.Function2[Boolean,js.Dynamic,Promise[A]]

  def execute(sparql: String, cb: CallBack[Any]): Unit = ???

  @JSName("execute")
  def executeUnit(sparql: String, cb: CallBack[Unit]): Unit = ???

  def delete(node: js.Dynamic, cb: CallBack[Unit]): Unit = ???

  def delete(node: js.Dynamic, triples: js.String, cb: CallBack[Unit]): Unit = ???

  def insert(node: js.Dynamic, cb: CallBack[Unit])

  def insert(node: js.Dynamic, triples: js.String, cb: CallBack[Unit]): Unit = ???

  def clear(cb: CallBack[Unit]): Unit = ???

  def clear(uri: js.String, cb: CallBack[Unit]): Unit = ???

  def graph(uri: js.String, cb: CallBack[JSStore#Graph]): Unit = ???

  // this function returns in the callback the number of triples read
  def load(mediaType: String, data: String, cb: CallBack[Int]): Unit = ???

  // this function returns in the callback the number of triples read
  def load(mediaType: String, data: String, uri: String, cb: CallBack[Int]): Unit = ???

  def rdf: js.Dynamic = ???

  //todo
  //def create()

}

//trait RDFLoader extends js.Object {
//    def tryToParse(parser: js.Any, textToParse: String,)
//}


//would be much better to use the RVN3InnerParser, which has a per triple callback
object RVN3Parser {
  type CallBack[A] = js.Function2[Boolean,js.Dynamic,Promise[A]]

  def parse(data: String, uri: String, cb: CallBack[JSStore#Graph]): Unit = ???
}

trait RDFJSTriple {
  def subject: js.Any
  def predicate: js.Any
  @JSName("object")
  def obj: js.Any
}

@JSName("RDFJSInterface.Graph")
class Graph() extends js.Object {

  def triples: Array[RDFJSTriple] = ???
  def length: Int = ???
  def add(triple: RDFJSTriple): this.type = ???
  def remove(triple: RDFJSTriple): this.type = ???
  def merge(g: Graph): Graph = ???
  def dup(): Graph = ???


}
