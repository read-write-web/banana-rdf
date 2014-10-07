package org.w3.banana.plantain

import java.io.{ OutputStream, StringWriter }

import org.openrdf.{ model => sesame }
import org.w3.banana._
import org.w3.banana.plantain.model_jvm.Triple._
import org.w3.banana.sesame.SesameSyntax

import scala.util.Try

class PlantainRDFWriter[T](ops: RDFOps[Plantain])(implicit sesameSyntax: SesameSyntax[T], _syntax: Syntax[T])
    extends RDFWriter[Plantain, T] {
  import ops._
  val syntax = _syntax

  def write(graph: Plantain#Graph, os: OutputStream, base: String): Try[Unit] = Try {
    val baseUri: Plantain#URI = makeUri(base)
    val sWriter = sesameSyntax.rdfWriter(os, base)
    sWriter.startRDF()
    graph.triples foreach { statement =>
      import statement._
      sWriter.handleStatement(
        asSesame(subject.relativizeAgainst(baseUri),
          predicate.relativizeAgainst(baseUri),
          objectt.relativizeAgainst(baseUri)))
    }
    sWriter.endRDF()
  }

  def asString(graph: Plantain#Graph, base: String): Try[String] = Try {
    val baseUri: Plantain#URI = makeUri(base)
    val result = new StringWriter()
    val sWriter = sesameSyntax.rdfWriter(result, base)
    sWriter.startRDF()
    graph.triples foreach { triple =>
      import triple._
      sWriter.handleStatement(
        asSesame(subject.relativizeAgainst(baseUri),
          predicate.relativizeAgainst(baseUri),
          objectt.relativizeAgainst(baseUri)))
    }
    sWriter.endRDF()
    result.toString
  }

}

class PlantainRDFWriterHelper(implicit ops: RDFOps[Plantain]) {

  implicit val rdfxmlWriter: RDFWriter[Plantain, RDFXML] = new PlantainRDFWriter[RDFXML](ops)

  implicit val turtleWriter: RDFWriter[Plantain, Turtle] = new PlantainRDFWriter[Turtle](ops)

  implicit val jsonldCompactedWriter: RDFWriter[Plantain, JsonLdCompacted] = new PlantainRDFWriter[JsonLdCompacted](ops)

  implicit val jsonldExpandedWriter: RDFWriter[Plantain, JsonLdExpanded] = new PlantainRDFWriter[JsonLdExpanded](ops)

  implicit val jsonldFlattenedWriter: RDFWriter[Plantain, JsonLdFlattened] = new PlantainRDFWriter[JsonLdFlattened](ops)

  val selector: RDFWriterSelector[Plantain] =
    RDFWriterSelector[Plantain, RDFXML] combineWith RDFWriterSelector[Plantain, Turtle] combineWith
      RDFWriterSelector[Plantain, JsonLdCompacted] combineWith RDFWriterSelector[Plantain, JsonLdExpanded] combineWith
      RDFWriterSelector[Plantain, JsonLdFlattened]

}