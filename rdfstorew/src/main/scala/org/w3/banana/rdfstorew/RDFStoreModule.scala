package org.w3.banana.rdfstorew

import org.w3.banana._
import org.w3.banana.io.{RDFWriter, RDFReader, Turtle}

import scala.concurrent.Future
import scala.util.Try

trait RDFStoreModule
    extends RDFModule
    with RDFOpsModule
    with RecordBinderModule
    with TurtleReaderFutureModule
    with TurtleWriterFutureModule {

  type Rdf = RDFStore

  implicit val store:GraphStore[Rdf,Future, scalajs.js.Dynamic] = RDFStoreW(Map())

  implicit val sparqlEngine:SparqlEngine[Rdf,Future, scalajs.js.Dynamic] = RDFStoreW(Map())

  implicit val ops: RDFOps[Rdf] = new RDFStoreOps

  implicit val sparqlOps: SparqlOps[Rdf] = RDFSparqlOps

  implicit val recordBinder: binder.RecordBinder[Rdf] = binder.RecordBinder[Rdf]

  implicit val turtleReader: RDFReader[Rdf, Future, Turtle] = new RDFStoreTurtleReader()(ops.asInstanceOf[RDFStoreOps])

  implicit val turtleWriter: RDFWriter[Rdf, Future, Turtle] = RDFStoreTurtleWriter

}
