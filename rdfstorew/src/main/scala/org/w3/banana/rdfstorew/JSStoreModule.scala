package org.w3.banana.rdfstorew

import org.w3.banana._
import org.w3.banana.io.{RDFWriter, RDFReader, Turtle}

import scala.concurrent.Future
import scala.util.Try
import scala.scalajs.js
import org.w3.banana.rdfstore.Store

trait JSStoreModule
    extends RDFModule
    with RDFOpsModule
    with RecordBinderModule
    with TurtleReaderFutureModule
    with TurtleWriterFutureModule {

  type Rdf = JSStore

  implicit val ops: RDFOps[Rdf] = new RDFStoreOps

  implicit val store: RDFStore[JSStore, Future, Store]
    with SparqlUpdate[JSStore, Future, Store]
  = RDFStoreJS.rdfStoreW

  implicit val sparqlEngine:SparqlEngine[Rdf,Future, Store] = store

  val jsstore: Store = new RDFStoreJS(Map()).rdfstorejs

  implicit val sparqlOps: SparqlOps[Rdf] = RDFSparqlOps

  implicit val recordBinder: binder.RecordBinder[Rdf] = binder.RecordBinder[Rdf]

  implicit val turtleReader: RDFReader[Rdf, Future, Turtle] = new RDFStoreTurtleReader()(ops.asInstanceOf[RDFStoreOps])

  implicit val turtleWriter: RDFWriter[Rdf, Future, Turtle] = RDFStoreTurtleWriter

}
