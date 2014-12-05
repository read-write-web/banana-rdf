package org.w3.banana.rdfstore.rjs

import org.w3.banana.rdfstorew.JSStore
import scala.concurrent.Promise
import scala.scalajs.js.annotation.JSName
import scala.scalajs.js
import org.w3.banana.rdfstore.rjs
import scala.scalajs.js.UndefOr

@JSName("Store.Store")
class Store(callback: js.Any = null, params: js.Any = null) extends js.Object {

  type CallBack = js.Function2[Boolean,js.Any,Any]

  def execute(sparql: String, cb: CallBack): Unit = ???

  @JSName("execute")
  def executeUnit(sparql: String, cb: CallBack): Unit = ???

  def delete(graph: rjs.Graph, cb: CallBack): Unit = ???

  def delete(graph: rjs.Graph, node: js.Any, cb: CallBack): Unit = ???

  def insert(graph: rjs.Graph, cb: CallBack)  = ???

  def insert(graph: rjs.Graph, node: js.Any, cb: CallBack): Unit = ???

  def clear(cb: CallBack): Unit = ???

  def clear(uri: js.String, cb: CallBack): Unit = ???

  def graph(callback: CallBack): Unit = ???

  def graph(uri: String, callback: CallBack): Unit = ???

  // this function returns in the callback the number of triples read
  def load(mediaType: String, data: String, cb: CallBack): Unit = ???

  // this function returns in the callback the number of triples read
  def load(mediaType: String, data: String, uri: String, cb: CallBack): Unit = ???

  def rdf: env = ???

  def engine: QueryEngine = ???



  //todo
  //def create()

}

//trait RDFLoader extends js.Object {
//    def tryToParse(parser: js.Any, textToParse: String,)
//}

trait Parser extends js.Object {
  def parse(data: String, uriOfBase: String, cb: js.Function2[js.Boolean,js.Any,Unit] ): Unit = ???
}

@JSName("QueryEngine.QueryEngine")
trait QueryEngine extends js.Object {
  val rdfLoader: RDFLoader = ???
}

@JSName("RDFLoader.RDFLoader")
trait RDFLoader extends js.Object {
  def parsers : js.Dictionary[Parser] = ???
}


@JSName("RDFJSInterface.RDFNode")
trait Node extends js.Object {
  val interfaceName: String = ???
  val nominalValue: String = ???
  val attributes: js.Array[String] = ???

  override def equals(other: scala.Any): Boolean = ???
  def toNT(): String = ???
}

@JSName("RDFJSInterface.BlankNode")
trait BlankNode extends Node {
  def bnodeId: String = ???
}

@JSName("RDFJSInterface.NamedNode")
trait NamedNode extends Node

@JSName("RDFJSInterface.Literal")
trait Literal extends Node {
  def datatype: String = ???
  def language: String = ???
}


@JSName("RDFJSInterface.Triple")
trait Triple extends js.Object  {
  //returning js.Dynamic below because I don't yet have time to fill those out
  def subject: rjs.Node = ???
  def predicate: rjs.Node = ???
  @JSName("object")
  def obj: rjs.Node = ???
  def equals(other: js.Any): Boolean = ???
}

@JSName("RDFJSInterface.Graph")
trait Graph extends js.Object {

  def triples: js.Array[rjs.Triple] = ???
  def length: Int = ???
  def add(triple: rjs.Triple): this.type = ???
  def remove(triple: rjs.Triple): this.type = ???
  def merge(g: rjs.Graph): rjs.Graph = ???
  def dup(): rjs.Graph = ???
  def filter(f: (rjs.Triple,rjs.Graph)=>Boolean): rjs.Graph = ???

  @JSName("match")
  def matches(s: rjs.Node, p: rjs.Node, o: rjs.Node, limit: UndefOr[Int]=null): rjs.Graph = ???

  def toNT(): String = ???

}


@JSName("RDFJSInterface.RDFEnvironment")
trait env extends js.Object  {

  def createBlankNode() : rjs.BlankNode = ???

  def createNamedNode(name: String): rjs.NamedNode = ???

  def createLiteral(value: String, language: String, datatype: rjs.NamedNode ): rjs.Literal = ???

  def createTriple(subject: rjs.Node, relation: rjs.Node, obj: rjs.Node ): rjs.Triple = ???

  def createGraph(triples: js.Array[rjs.Triple]): rjs.Graph = ???
  def createGraph(): rjs.Graph = ???

}