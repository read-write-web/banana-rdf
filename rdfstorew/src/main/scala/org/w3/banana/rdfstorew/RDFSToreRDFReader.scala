package org.w3.banana.rdfstorew

import java.io.InputStream

import org.w3.banana._
import org.w3.banana.io._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.Try

class RDFStoreTurtleReader(implicit ops: RDFStoreOps) extends RDFReader[RDFStore, Try, Turtle] {

  val syntax = Syntax[Turtle]

  override def read(text: String, base: String): Future[RDFStore#Graph] =
    ops.load(RDFStoreW.rdfstorejs, "text/turtle", text, base)

  /**
   * legacy: if one passes an input stream at this layer one
   * would need to know the encoding too. This function is badly designed.
   */
  override def read(is: InputStream, base: String) = ???

}
