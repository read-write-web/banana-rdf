package org.w3.banana.rdfstorew

import org.w3.banana._
import org.w3.banana.io.{RDFWriter, RDFReader, Turtle}

import scala.concurrent.Future
import scala.util.Try
import scala.scalajs.js
import org.w3.banana.rdfstore.rjs

trait JSStoreModule
    extends RDFModule
    with RDFOpsModule
    with RecordBinderModule
    with TurtleReaderFutureModule
    with TurtleWriterFutureModule {

  type Rdf = JSStore

  val rdfstoreOps: RDFStoreOps  = new RDFStoreOps
  implicit val ops: RDFOps[Rdf] = rdfstoreOps

  implicit val store: RDFStore[JSStore, Future, rjs.Store]
    with SparqlUpdate[JSStore, Future, rjs.Store]
  = RDFStoreJS.rdfStoreW

  implicit val sparqlEngine:SparqlEngine[Rdf,Future, rjs.Store] = store

  lazy val jsstore: rjs.Store = new RDFStoreJS(Map()).rdfstorejs
  lazy val rdf : rjs.env = jsstore.rdf

  implicit val sparqlOps: SparqlOps[Rdf] = RDFSparqlOps

  implicit val recordBinder: binder.RecordBinder[Rdf] = binder.RecordBinder[Rdf]

  implicit val turtleReader: RDFReader[Rdf, Future, Turtle] = new RDFStoreTurtleReader()

  implicit val turtleWriter: RDFWriter[Rdf, Future, Turtle] = RDFStoreTurtleWriter

}
