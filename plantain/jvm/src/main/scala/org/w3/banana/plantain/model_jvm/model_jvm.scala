package org.w3.banana.plantain.model_jvm

import akka.http.model.Uri
import info.aduna.iteration.CloseableIteration
import org.openrdf.model.impl._
import org.openrdf.model.{BNode => SesameBNode, Graph => SesameGraph, Literal => SesameLiteral, URI => SesameURI, _}
import org.openrdf.query.QueryEvaluationException
import org.openrdf.query.algebra.evaluation.TripleSource
import org.w3.banana.plantain.{Plantain, model}

object Node {
  val xmls = model.URI(Uri("http://www.w3.org/2001/XMLSchema#string"))
  import org.w3.banana.plantain.PlantainOps.{makeLang, makeUri, _}

  def fromSesame(value: Value): Plantain#Node = value match {
    case resource: Resource => fromSesame(resource)
    case literal: SesameLiteral => fromSesame(literal)
  }
  def fromSesame(uri: SesameURI): Plantain#URI = makeUri(uri.toString)
  def fromSesame(resource: Resource): model.Node = resource match {
    case uri: SesameURI => fromSesame(uri)
    case bnode: SesameBNode => BNode(bnode.getID)
  }
  def fromSesame(literal: SesameLiteral): Plantain#Literal = {
    val lexicalForm = literal.getLabel
    val lang = literal.getLanguage
    val langOpt = if (lang == null || lang.isEmpty) None else Some(makeLang(lang))
    val typ = Option(literal.getDatatype).map(u => makeUri(u.toString)) getOrElse xmls
    org.w3.banana.plantain.model.Literal[Uri](lexicalForm, typ, langOpt)
  }
  def fromSesame(statement: Statement): Plantain#Triple = {
    org.w3.banana.plantain.model.Triple(
      fromSesame(statement.getSubject),
      fromSesame(statement.getPredicate),
      fromSesame(statement.getObject))
  }
  implicit def asSesame(node: model.Node): Value = node match {
    case URI(uriS) => {
      try {
        new URIImpl(uriS)
      } catch {
        case iae: IllegalArgumentException =>
          new SesameURI {
            override def equals(o: Any): Boolean = o.isInstanceOf[SesameURI] && o.asInstanceOf[SesameURI].toString == uriS
            def getLocalName: String = uriS
            def getNamespace: String = ""
            override def hashCode: Int = uriS.hashCode
            override def toString: String = uriS
            def stringValue: String = uriS
          }
      }
    }
    case BNode(label) => new BNodeImpl(label)
    case Literal(lexicalForm, uri, None) => new LiteralImpl(lexicalForm, new URIImpl(uri.underlying.toString))
    case Literal(lexicalForm, _, Some(lang)) => new LiteralImpl(lexicalForm, lang)
  }
}

object Graph {
  val vf: ValueFactory = ValueFactoryImpl.getInstance()
  val empty = org.w3.banana.plantain.model.Graph[Uri](Map.empty, 0)
  implicit def toTripleSource(g: model.Graph[Uri]): TripleSource = new TripleSource {
    override def getStatements(subject: Resource,
      predicate: SesameURI,
      objectt: Value,
      contexts: Resource*): CloseableIteration[_ <: Statement, QueryEvaluationException] = {
      if (contexts.nonEmpty)
        throw new UnsupportedOperationException
      else {
        val s = if (subject == null) model.ANY else model.PlainNode(Node.fromSesame(subject))
        val p = if (predicate == null) model.ANY else model.PlainNode(Node.fromSesame(predicate))
        val o = if (objectt == null) model.ANY else model.PlainNode(Node.fromSesame(objectt))
        val it = g.find(s, p, o).iterator
        new CloseableIteration[Statement, QueryEvaluationException] {
          def close(): Unit = ()
          def hasNext(): Boolean = it.hasNext
          def next(): Statement = Triple.asSesame(it.next())
          def remove(): Unit = throw new UnsupportedOperationException
        }
      }
    }
    override def getValueFactory = vf
  }
}

object Triple {
  def fromSesame(statement: Statement): org.w3.banana.plantain.model.Triple[Uri] =
    org.w3.banana.plantain.model.Triple[Uri](
      Node.fromSesame(statement.getSubject),
      model.URI(Uri(statement.getPredicate.toString)),
      Node.fromSesame(statement.getObject))

  def asSesame(subject: model.Node, predicate: model.URI[Uri], objectt: model.Node) : Statement =  {
    new StatementImpl(
      Node.asSesame(subject).asInstanceOf[Resource],
      Node.asSesame(predicate).asInstanceOf[SesameURI],
      Node.asSesame(objectt))
  }

  def asSesame(triple: org.w3.banana.plantain.model.Triple[Uri]): Statement = {
    import triple._
    asSesame(subject,predicate,objectt)
  }
}