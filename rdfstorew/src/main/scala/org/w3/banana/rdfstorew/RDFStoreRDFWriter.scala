package org.w3.banana.rdfstorew

import java.io.OutputStream

import org.w3.banana.io.{RDFWriter, Syntax, Turtle}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

import scala.concurrent.Future
//import scala.util.Try

object RDFStoreTurtleWriter extends RDFWriter[RDFStore, Future, Turtle] {

  val syntax:  Syntax[Turtle] = Syntax.Turtle

  def asString(graph: RDFStore#Graph, base: String): Future[String] = Future {
    graph.graph.toNT().asInstanceOf[String]
  }

  override def write(obj: RDFStore#Graph, outputstream: OutputStream, base: String) = ???
}
