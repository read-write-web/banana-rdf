package org.w3.banana.plantain

import org.w3.banana._

trait PlantainModule
    extends RDFModule
    with RDFOpsModule
    with RecordBinderModule
    with TurtleReaderModule
    with RDFXMLReaderModule
    with JsonLDReaderModule
    with TurtleWriterModule
    with RDFXMLWriterModule
    with JsonLDWriterModule
    with ReaderSelectorModule
    with WriterSelectorModule
    with SparqlOpsModule
    with SparqlGraphModule
    with XmlSolutionsWriterModule
    with JsonSolutionsWriterModule
    with XmlQueryResultsReaderModule
    with JsonQueryResultsReaderModule {

  type Rdf = Plantain

  implicit val ops: RDFOps[Plantain] = PlantainOps

  implicit val recordBinder: binder.RecordBinder[Plantain] = binder.RecordBinder[Plantain]

  implicit val turtleReader: RDFReader[Plantain, Turtle] = new PlantainTurtleReader

  implicit val rdfXMLReader: RDFReader[Plantain, RDFXML] = new PlantainRDFXMLReader

  implicit val jsonldCompactReader: RDFReader[Plantain, JsonLdCompacted] = new PlantainJSONLDCompactedReader

  implicit val jsonldExpandedReader: RDFReader[Plantain, JsonLdExpanded] = new PlantainJSONLDExpandedReader

  implicit val jsonldFlattenedReader: RDFReader[Plantain, JsonLdFlattened] = new PlantainJSONLDFlattenedReader

  val plantainRDFWriterHelper = new PlantainRDFWriterHelper

  implicit val rdfXMLWriter: RDFWriter[Plantain, RDFXML] = plantainRDFWriterHelper.rdfxmlWriter

  implicit val turtleWriter: RDFWriter[Plantain, Turtle] = plantainRDFWriterHelper.turtleWriter

  implicit val jsonldCompactedWriter: RDFWriter[Plantain, JsonLdCompacted] = plantainRDFWriterHelper.jsonldCompactedWriter

  implicit val jsonldExpandedWriter: RDFWriter[Plantain, JsonLdExpanded] = plantainRDFWriterHelper.jsonldExpandedWriter

  implicit val jsonldFlattenedWriter: RDFWriter[Plantain, JsonLdFlattened] = plantainRDFWriterHelper.jsonldFlattenedWriter

  implicit val sparqlOps: SparqlOps[Plantain] = PlantainSparqlOps

  implicit val sparqlGraph: SparqlEngine[Plantain, Plantain#Graph] = PlantainGraphSparqlEngine()

  implicit val xmlSolutionsWriter: SparqlSolutionsWriter[Plantain, SparqlAnswerXml] =
    PlantainSolutionsWriter.solutionsWriterXml

  implicit val jsonSolutionsWriter: SparqlSolutionsWriter[Plantain, SparqlAnswerJson] =
    PlantainSolutionsWriter.solutionsWriterJson

  implicit val xmlQueryResultsReader: SparqlQueryResultsReader[Plantain, SparqlAnswerXml] =
    PlantainQueryResultsReader.queryResultsReaderXml

  implicit val jsonQueryResultsReader: SparqlQueryResultsReader[Plantain, SparqlAnswerJson] =
    PlantainQueryResultsReader.queryResultsReaderJson

  implicit val readerSelector: ReaderSelector[Plantain] = ReaderSelector[Plantain, Turtle] combineWith
    ReaderSelector[Plantain, RDFXML] combineWith ReaderSelector[Plantain, JsonLdCompacted] combineWith
    ReaderSelector[Plantain, JsonLdExpanded] combineWith ReaderSelector[Plantain, JsonLdFlattened]

  implicit val writerSelector: RDFWriterSelector[Plantain] = plantainRDFWriterHelper.selector

}
