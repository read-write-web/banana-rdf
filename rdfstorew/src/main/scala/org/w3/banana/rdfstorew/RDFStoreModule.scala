package org.w3.banana.rdfstorew

import org.w3.banana._
import org.w3.banana.io.{RDFWriter, RDFReader, Turtle}

import scala.concurrent.Future
import scala.util.Try

trait RDFStoreModule
    extends RDFModule
    with RDFOpsModule
    with RecordBinderModule
    with TurtleReaderModule
    with TurtleWriterModule {

  type Rdf = RDFStore

  implicit val store:GraphStore[Rdf,Future, scalajs.js.Dynamic] = RDFStoreW(Map())

  implicit val sparqlEngine:SparqlEngine[Rdf,Future, scalajs.js.Dynamic] = RDFStoreW(Map())

  implicit val ops: RDFStoreOps = new RDFStoreOps

  implicit val sparqlOps: SparqlOps[RDFStore] = RDFSparqlOps

  implicit val recordBinder: binder.RecordBinder[RDFStore] = binder.RecordBinder[RDFStore]

  implicit val turtleReader: RDFReader[RDFStore, Future, Turtle] = new RDFStoreTurtleReader

  implicit val turtleWriter: RDFWriter[RDFStore, Future, Turtle] = RDFStoreTurtleWriter

}
