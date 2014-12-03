package org.w3.banana.rdfstorew

import java.io.{BufferedReader, Reader, InputStream}

import org.w3.banana.io._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import org.w3.banana.RDFOps

//import scala.util.Try

class RDFStoreTurtleReader(implicit ops: RDFStoreOps) extends RDFReader[RDFStore, Future, Turtle] {

  val syntax = Syntax[Turtle]

  override def read(is:InputStream, base: String): Future[RDFStore#Graph] = {
    val text = scala.io.Source.fromInputStream(is).mkString;
    ops.load(RDFStoreW.rdfstorejs, "text/turtle", text, base)//.value.get
  }

  /** Tries parsing an RDF Graph from a [[Reader]] and a base URI.
    * @param base the base URI to use, to resolve relative URLs found in the InputStream
    * */
  override def read(reader: Reader, base: String): Future[RDFStore#Graph] = {
    val text = Stream.continually(new BufferedReader(reader).readLine()).takeWhile(_ != null).toString()
    ops.load(RDFStoreW.rdfstorejs, "text/turtle", text, base)//.value.get
  }
}
