package org.w3.banana.rdfstorew

import java.io.OutputStream

import org.w3.banana.io.{RDFWriter, Syntax, Turtle}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

import scala.concurrent.Future
//import scala.util.Try

object RDFStoreTurtleWriter extends RDFWriter[JSStore, Future, Turtle] {

  val syntax:  Syntax[Turtle] = Syntax.Turtle

  def asString(graph: JSStore#Graph, base: String): Future[String] = Future {
    graph.graph.toNT().asInstanceOf[String]
  }

  override def write(obj: JSStore#Graph, outputstream: OutputStream, base: String) = ???
}
