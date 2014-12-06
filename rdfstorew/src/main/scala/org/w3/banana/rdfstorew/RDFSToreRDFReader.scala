package org.w3.banana.rdfstorew

import java.io.{BufferedReader, Reader, InputStream}

import org.w3.banana.io._

import scala.concurrent.{Promise, Future}
import org.w3.banana.{rdfstorew, RDFOps}
import scala.scalajs.js
import org.w3.banana.rdfstore.rjs.Triple

//import scala.util.Try

class RDFStoreTurtleReader extends RDFReader[JSStore, Future, Turtle] {

  override def read(is: InputStream, base: String): Future[JSStore#Graph] = {
    val ttl = scala.io.Source.fromInputStream(is).mkString;
    parse( ttl, base)
  }


    def parse(turtleText: String, base: String): Future[JSStore#Graph] = {
      import JSStore.ops._
      //the run-now execution context should be fine as the two methods below work with callbacks
      //that presumably uses javascripts task queue (
      //todo: to be verified
      import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
      for {
        i <- JSStore.rdfstoreOps.load(JSStore.jsstore, "text/turtle", turtleText, base)
        g <- JSStore.store.getGraph(JSStore.jsstore, URI(base))
      } yield g
    }

//  avoiding the store to parse stuff here is not as easy as the following
//  attempt thought it would be
//
//  def parse(turtleText: String, base: String): Future[JSStore#Graph] = {
//    val promise = Promise[JSStore#Graph]()
//
//    import JSStore.jsstore._
//
//    val l = engine.rdfLoader
//    val p= l.parsers("text/turtle")
//
//    val callback = (success: Boolean, answer: js.Any) => {
//      if (success) {
//        val graph = rdf.createGraph
//        for (triple <- answer.asInstanceOf[js.Array[rjs.Triple]] ) {
//           //would need to transform the internal quad representation to the standard one
//           // and it is more work than one would simply think
//        }
//        promise.success(new RDFStoreGraph(graph))
//      }
//      else promise.failure(new Throwable(answer.toString))
//      ()
//    }
//    p.parse(turtleText,base,callback  )
//    promise.future
//  }

  /** Tries parsing an RDF Graph from a [[Reader]] and a base URI.
    * @param base the base URI to use, to resolve relative URLs found in the InputStream
    * */
  override def read(reader: Reader, base: String): Future[JSStore#Graph] = {
    val ttl = reader match {
      case org.w3.banana.rdfstorew.StringReader(string) => string
      case other =>  Stream.continually(new BufferedReader(reader).readLine()).takeWhile(_ != null).toString()
    }
    parse( ttl, base)
  }
}

case class StringReader(string: String) extends java.io.StringReader(string)
