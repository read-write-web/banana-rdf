package org.w3.banana.rdfstorew

import org.w3.banana._

trait RDFStoreModule
    extends RDFModule
    with RDFOpsModule
    with RecordBinderModule
    with TurtleReaderModule
    with TurtleWriterModule {

  type Rdf = RDFStore

  implicit val store:GraphStore[Rdf,scalajs.js.Dynamic] = RDFStoreW(Map())

  implicit val sparqlEngine:SparqlEngine[Rdf,scalajs.js.Dynamic] = RDFStoreW(Map())

  implicit val ops: RDFStoreOps = new RDFStoreOps

  implicit val sparqlOps: SparqlOps[RDFStore] = RDFSparqlOps

  implicit val recordBinder: binder.RecordBinder[RDFStore] = binder.RecordBinder[RDFStore]

  implicit val turtleReader: RDFReader[RDFStore, Turtle] = new RDFStoreTurtleReader

  implicit val turtleWriter: RDFWriter[RDFStore, Turtle] = RDFStoreTurtleWriter

}
