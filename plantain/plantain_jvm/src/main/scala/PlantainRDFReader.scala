package org.w3.banana.plantain

import java.io.InputStream

import akka.http.model.Uri
import org.openrdf.rio._
import org.openrdf.rio.rdfxml.RDFXMLParser
import org.openrdf.rio.turtle._
import org.openrdf.{ model => sesame }
import org.w3.banana._

import scala.util.Try

object PlantainRDFXMLReader extends RDFReader[Plantain, RDFXML] {

  val syntax: Syntax[RDFXML] = Syntax.RDFXML

  /**
   * todo: this is the wrong way around. The reader taking an inputstream should
   * call the reader taking a string or better a StringReader ( but does not exist yet in scala-js)
   * @param is
   * @param base
   * @return
   */
  def read(is: InputStream, base: String): Try[model.Graph[Uri]] = Try {
    val sink = new Sink
    val parser = new RDFXMLParser
    parser.setRDFHandler(sink)
    parser.parse(is, base)
    sink.graph
  }

}

object PlantainTurtleReader extends RDFReader[Plantain, Turtle] {

  val syntax: Syntax[Turtle] = Syntax.Turtle

  /**
   * todo: this is the wrong way around. The reader taking an inputstream should
   * call the reader taking a string or better a StringReader ( but does not exist yet in scala-js)
   * @param is
   * @param base
   * @return
   */
  def read(is: InputStream, base: String): Try[model.Graph[Uri]] = Try {
    val sink = new Sink
    val parser = new TurtleParser
    parser.setRDFHandler(sink)
    parser.parse(is, base)
    sink.graph
  }
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
