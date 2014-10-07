package org.w3.banana.plantain

import java.io.InputStream

import akka.http.model.Uri
import com.github.jsonldjava.sesame.SesameJSONLDParser
import org.openrdf.rio._
import org.openrdf.rio.helpers.{JSONLDSettings, JSONLDMode}
import org.openrdf.rio.turtle._
import org.openrdf.{model => sesame}
import org.w3.banana._

import scala.util.Try

abstract class AbstractPlantainReader[Syntax] extends RDFReader[Plantain,Syntax] {

  implicit def ops: RDFOps[Plantain]

  def getParser(): org.openrdf.rio.RDFParser

  /**
   * todo: this is the wrong way around. The reader taking an inputstream should
   * call the reader taking a string or better a StringReader ( but does not exist yet in scala-js)
   * @param is
   * @param base
   * @return
   */
  def read(is: InputStream, base: String): Try[Plantain#Graph] = Try {
    val sink = new Sink
    val parser = getParser()
    parser.setRDFHandler(sink)
    parser.parse(is, base)
    sink.graph
  }

}

class PlantainRDFXMLReader(implicit val ops: RDFOps[Plantain]) extends AbstractPlantainReader[RDFXML] {

  val syntax: Syntax[RDFXML] = Syntax.RDFXML
  def getParser() = new org.openrdf.rio.rdfxml.RDFXMLParser

}

class PlantainTurtleReader(implicit val ops: RDFOps[Plantain]) extends AbstractPlantainReader[Turtle] {
   val syntax: Syntax[Turtle] = Syntax.Turtle
   def getParser() = new TurtleParser()
}

/**
 * Note: an issue with the com.github.jsonldjava is apparently that it
 * loads the whole JSON file into memory, which is memory consumptive
 */
trait AbstractPlantainJSONLDReader[T] extends AbstractPlantainReader[T] {

  def jsonldProfile: JSONLDMode

  def getParser() = {
    val parser = new SesameJSONLDParser
    parser.getParserConfig.set(JSONLDSettings.JSONLD_MODE, jsonldProfile)
    parser
  }

}

class PlantainJSONLDCompactedReader(implicit val ops: RDFOps[Plantain]) extends AbstractPlantainJSONLDReader[JsonLdCompacted] {
  val syntax = Syntax[JsonLdCompacted]
  val jsonldProfile = JSONLDMode.COMPACT
}

class PlantainJSONLDExpandedReader(implicit val ops: RDFOps[Plantain]) extends AbstractPlantainJSONLDReader[JsonLdExpanded] {
  val syntax = Syntax[JsonLdExpanded]
  val jsonldProfile = JSONLDMode.EXPAND
}

class PlantainJSONLDFlattenedReader(implicit val ops: RDFOps[Plantain]) extends AbstractPlantainJSONLDReader[JsonLdFlattened] {
  val syntax = Syntax[JsonLdFlattened]
  val jsonldProfile = JSONLDMode.FLATTEN
}


class Sink(var graph: model.Graph[Uri] = PlantainOps.emptyGraph,
  var prefixes: Map[String, String] = Map.empty)
    extends RDFHandler {
  import org.w3.banana.plantain.PlantainOps._

  def startRDF(): Unit = ()
  def endRDF(): Unit = ()
  def handleComment(comment: String): Unit = ()
  def handleNamespace(prefix: String, uri: String): Unit = prefixes += (prefix -> uri)
  def handleStatement(statement: org.openrdf.model.Statement): Unit = {
    val s: model.Node = statement.getSubject match {
      case bnode: sesame.BNode => makeBNodeLabel(bnode.getID)
      case uri: sesame.URI => makeUri(uri.toString)
    }
    val p: model.URI[Uri] = statement.getPredicate match {
      case uri: sesame.URI => model.URI(Uri(uri.toString))
    }
    val o: model.Node = statement.getObject match {
      case bnode: sesame.BNode => makeBNodeLabel(bnode.getID)
      case uri: sesame.URI => makeUri(uri.toString)
      case literal: sesame.Literal => model.Literal[Uri](literal.stringValue, makeUri(literal.getDatatype.stringValue), Option(literal.getLanguage))
    }
    this.graph = this.graph + Triple(s, p, o)
  }

}
