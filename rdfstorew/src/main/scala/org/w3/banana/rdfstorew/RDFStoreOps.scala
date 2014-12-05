package org.w3.banana.rdfstorew

import org.w3.banana.isomorphism.{VerticeCBuilder, SimpleMappingGenerator, GraphIsomorphism}
import org.w3.banana.{ RDFOps, URIOps }
import java.net.{ URI => jURI }

import scala.concurrent.Promise
import scala.concurrent.Future
import scala.scalajs.js
import scala.sys.process.ProcessLogger
import org.w3.banana.rdfstore.rjs
import org.w3.banana.rdfstore.rjs.env

trait JSUtils {
  def log(obj: RDFStoreRDFNode) = js.Dynamic.global.console.log(obj.jsNode)

  def log(obj: RDFStoreTriple) = js.Dynamic.global.console.log(obj.triple.asInstanceOf[js.Dynamic])

  def log(obj: RDFStoreGraph) = js.Dynamic.global.console.log(obj.graph.asInstanceOf[js.Dynamic])

  def log(obj: js.Dynamic) = {
    js.Dynamic.global.console.log(obj)
  }

  def log(obj: String) = js.Dynamic.global.console.log(obj)

  def global = js.Dynamic.global
}

trait RDFStoreURIOps extends URIOps[JSStore] {

  private def java(uri: JSStore#URI): jURI = new jURI(uri.valueOf)

  def rdfjs(uri: jURI): JSStore#URI = {
    (new RDFStoreOps()).makeUri(uri.toString)
  }

  def getString(uri: JSStore#URI): String = java(uri).toString

  def withoutFragment(uri: JSStore#URI): JSStore#URI = {
    (new RDFStoreOps()).makeUri(uri.valueOf.split("#")(0))
  }

  def withFragment(uri: JSStore#URI, frag: String): JSStore#URI = {
    val u = java(uri)
    import u._
    rdfjs(new jURI(getScheme, getUserInfo, getHost, getPort, getPath, getQuery, frag))
  }

  def getFragment(uri: JSStore#URI): Option[String] = Option(java(uri).getFragment)

  def isPureFragment(uri: JSStore#URI): Boolean = {
    val u = java(uri)
    import u.{getFragment => fragment, _}
    getScheme == null &&
      getUserInfo == null && getAuthority == null &&
      (getPath == null || getPath == "") &&
      getQuery == null && fragment != null

  }

  def resolve(uri: JSStore#URI, other: JSStore#URI): JSStore#URI =
    rdfjs(java(uri).resolve(java(other)))

  def appendSegment(uri: JSStore#URI, segment: String): JSStore#URI = {
    val u = java(uri)
    val path = u.getPath
    val newpath = if (path.endsWith("/")) path + segment else path + "/" + segment
    import u._
    val res = rdfjs(new jURI(getScheme, getUserInfo, getHost, getPort, newpath, getQuery, null))
    res
  }

  def relativize(uri: JSStore#URI, other: JSStore#URI): JSStore#URI = {
    val result = new jURI(uri.nominalValue).relativize(new jURI(other.nominalValue))
    (new RDFStoreOps()).makeUri(result.toString)
  }

  def lastSegment(uri: JSStore#URI): String = {
    val path = java(uri).getPath
    val i = path.lastIndexOf('/')
    path.substring(i + 1, path.length)
  }
}

class RDFStoreOps extends RDFOps[JSStore] with RDFStoreURIOps with JSUtils {
  import JSStore._

  override def emptyGraph: JSStore#Graph = new RDFStoreGraph(JSStore.rdf.createGraph())

  override implicit def toConcreteNodeMatch(node: JSStore#Node): JSStore#NodeMatch = PlainNode(node)

  override def diff(g1: JSStore#Graph, g2: JSStore#Graph): JSStore#Graph = throw new Exception("DIFF NOT IMPLEMENTED")

  override def fromTriple(triple: JSStore#Triple): (JSStore#Node, JSStore#URI, JSStore#Node) = (triple.subject, triple.predicate, triple.objectt)

  override def makeBNode(): JSStore#BNode = new RDFStoreBlankNode(JSStore.rdf.createBlankNode())

  def graphToIterable(graph: JSStore#Graph): Iterable[JSStore#Triple] = graph.triples

  override def foldNode[T](node: JSStore#Node)(funURI: (JSStore#URI) => T, funBNode: (JSStore#BNode) => T, funLiteral: (JSStore#Literal) => T): T = node.jsNode.interfaceName.asInstanceOf[js.String] match {
    case "NamedNode" => funURI(node.asInstanceOf[RDFStoreNamedNode])
    case "BlankNode" => funBNode(node.asInstanceOf[RDFStoreBlankNode])
    case "Literal" => funLiteral(node.asInstanceOf[RDFStoreLiteral])
  }

  override def makeLang(s: String): JSStore#Lang = s

  override def makeBNodeLabel(s: String): JSStore#BNode =
    new RDFStoreBlankNode(JSStore.rdf.createBlankNode())

  override def makeLangTaggedLiteral(lexicalForm: String, lang: JSStore#Lang): JSStore#Literal =
    new RDFStoreLiteral(JSStore.rdf.createLiteral(lexicalForm, lang, null))

  override def fromLiteral(literal: JSStore#Literal): (String, JSStore#URI, Option[JSStore#Lang]) = {
    val lexicalForm: String = literal.nominalValue.asInstanceOf[String]
    val datatype: JSStore#URI = if (literal.datatype == null) {
      null
    } else {
      makeUri(literal.datatype)
    }
    var lang: Option[JSStore#Lang] = if (literal.language == null) {
      None
    } else {
      Some(literal.language.asInstanceOf[JSStore#Lang])
    }

    (lexicalForm, datatype, lang)
  }

  override def makeUri(s: String): JSStore#URI = {
    new RDFStoreNamedNode(JSStore.rdf.createNamedNode(s))
  }

  override def makeTriple(s: JSStore#Node, p: JSStore#URI, o: JSStore#Node): JSStore#Triple = {
    new RDFStoreTriple(JSStore.rdf.createTriple(s.jsNode, p.jsNode, o.jsNode))
  }

  override def ANY: JSStore#NodeAny = JsANY

  override def makeGraph(it: Iterable[JSStore#Triple]): JSStore#Graph = {
    val triplesArray = js.Array[rjs.Triple]()
    for (triple <- it) {
      triplesArray.push(triple.triple)
    }

    new RDFStoreGraph(JSStore.rdf.createGraph(triplesArray))
  }

  override def fromLang(l: JSStore#Lang): String = l

  override def foldNodeMatch[T](nodeMatch: JSStore#NodeMatch)(funANY: => T, funNode: (JSStore#Node) => T): T =
    nodeMatch match {
      case PlainNode(node) => funNode(node)
      case _ => funANY
    }

  private lazy val iso = new GraphIsomorphism[JSStore](new SimpleMappingGenerator[JSStore](VerticeCBuilder.simpleHash))(new RDFStoreOps)

  override def isomorphism(left: JSStore#Graph, right: JSStore#Graph): Boolean = iso.findAnswer(left, right).isSuccess


  def graphSize(g: JSStore#Graph): Int = g.size

  override def find(graph: RDFStoreGraph, subject: JSStore#NodeMatch, predicate: JSStore#NodeMatch, objectt: JSStore#NodeMatch): Iterator[JSStore#Triple] = {

    val subjectNode: rjs.Node = subject match {
      case PlainNode(node) => node.jsNode
      case x => null
    }

    val predicateNode: rjs.Node = predicate match {
      case PlainNode(node) => node.jsNode
      case x => null
    }

    val objectNode: rjs.Node = objectt match {
      case PlainNode(node) => node.jsNode
      case x => null
    }

    var filtered: js.Array[rjs.Triple] = graph.graph.matches(subjectNode, predicateNode, objectNode).triples
    var filteredList: List[JSStore#Triple] = List[JSStore#Triple]()
    for (triple <- filtered) {
      filteredList = filteredList.::(new RDFStoreTriple(triple))
    }

    filteredList.toIterator
  }

  override def fromBNode(bn: JSStore#BNode): String = bn.toString()

  override def fromUri(uri: JSStore#URI): String = getString(uri)

  // graph union
  override def union(graphs: Seq[JSStore#Graph]): JSStore#Graph = graphs.fold(emptyGraph)(_ merge _)

  override def makeLiteral(lexicalForm: String, datatype: RDFStoreNamedNode): JSStore#Literal = {
    var value = lexicalForm
    var lang: String = null

    if (lexicalForm.indexOf("@") != -1) {
      val parts = lexicalForm.split("@")
      value = parts(0)
      lang = parts(1)
    }

    val datatypeString = if (datatype != null) datatype.node else null

    new RDFStoreLiteral(JSStore.rdf.createLiteral(value, lang, datatypeString))
  }

  override def getTriples(graph: JSStore#Graph): Iterable[JSStore#Triple] = graphToIterable(graph)

  def load(store: rjs.Store, mediaType: String, data: String, graphUri: String = null): Future[JSStore#Graph] = {
    val promise = Promise[JSStore#Graph]
    val cb: js.Function2[Boolean,js.Any,Any] = (success: Boolean, numTriplesUploadedOrError: Any) =>
      if (success) {
        store.graph(graphUri, {
          (success: Boolean, graphOrError: Any) =>
            if (success) promise.success(new RDFStoreGraph(graphOrError.asInstanceOf[rjs.Graph]))
            else promise.failure(new Exception("could not extract data from database " + graphOrError))
        })
      } else promise.failure(new Exception("Error loading data into the store: " + numTriplesUploadedOrError))


    if (graphUri == null) {
      store.load(mediaType, data, cb)
    } else {
      store.load(mediaType, data, graphUri, cb)
    }

    promise.future
  }

}
