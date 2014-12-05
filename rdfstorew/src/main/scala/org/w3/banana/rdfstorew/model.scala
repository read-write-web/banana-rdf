package org.w3.banana.rdfstorew

import scala.scalajs.js
import org.w3.banana.rdfstore.rjs

class RDFStoreRDFNode(val jsNode: rjs.Node) {


  val interfaceName: js.String = if (null == jsNode.interfaceName) null else jsNode.interfaceName
  val attributes: js.Array[js.String] = if (null == jsNode.attributes) null else jsNode.attributes

  override def equals(other: Any): Boolean = {
    if (other.isInstanceOf[RDFStoreRDFNode]) jsNode.equals(other.asInstanceOf[RDFStoreRDFNode].jsNode) else false
  }

  override def toString: js.String = jsNode.toString()

  def toNT: js.String = jsNode.toNT().asInstanceOf[js.String]

  def valueOf: js.String = jsNode.valueOf().asInstanceOf[js.String]
}

class RDFStoreBlankNode(val node: rjs.BlankNode) extends RDFStoreRDFNode(node) {

  val bnodeId: js.String = node.bnodeId

}

class RDFStoreLiteral(val node: rjs.Literal) extends RDFStoreRDFNode(node) {

  val nominalValue: js.String = if (node.nominalValue.isInstanceOf[js.String]) { node.nominalValue.asInstanceOf[js.String] } else { null }
  val datatype: js.String = if (node == null) null else node.datatype
  val language: js.String = if (null == node.language) null else node.language.asInstanceOf[js.String]

}

class RDFStoreNamedNode(val node: rjs.NamedNode) extends RDFStoreRDFNode(node) {

  val nominalValue: js.String = node.nominalValue

}

class RDFStoreTriple(node: rjs.Triple) {
  val triple = node.asInstanceOf[rjs.Triple]

  def nodeType(node: rjs.Node) = node.interfaceName  match {
    case "BlankNode" =>
      new RDFStoreBlankNode(node.asInstanceOf[rjs.BlankNode])
    case "Literal" =>
      new RDFStoreLiteral(node.asInstanceOf[rjs.Literal])
    case "NamedNode" =>
      new RDFStoreNamedNode(node.asInstanceOf[rjs.NamedNode])
    case _ =>
      throw new Exception("Unknown RDF JS interface " + node.interfaceName)
  }

  val subject: RDFStoreRDFNode =     nodeType(triple.subject)


  val predicate: RDFStoreNamedNode = nodeType(triple.predicate).asInstanceOf[RDFStoreNamedNode]

  val objectt: RDFStoreRDFNode = nodeType(triple.obj)

  override def equals(other: Any): Boolean = {
    if (other.isInstanceOf[RDFStoreTriple])
      triple.equals(other.asInstanceOf[RDFStoreTriple].triple)
    else
      false
  }

  override def toString: js.String = node.toString()
}

class RDFStoreGraph(val graph: rjs.Graph) {
  def node = graph.asInstanceOf[js.Dynamic]
  def triples: js.Array[RDFStoreTriple] = {
    graph.triples.map((t: rjs.Triple) => new RDFStoreTriple(t))
  }

  def add(triple: RDFStoreTriple): RDFStoreGraph = {
    graph.add(triple.triple)
    this
  }

  def remove(triple: RDFStoreTriple): RDFStoreGraph = {
    graph.remove(triple.triple)
    this
  }

  def merge(other: RDFStoreGraph): RDFStoreGraph = new RDFStoreGraph(graph.merge(other.graph))

  override def equals(other: Any): Boolean = {
    if (other.isInstanceOf[RDFStoreGraph])
      graph.asInstanceOf[js.Dynamic].applyDynamic("equals")(other.asInstanceOf[RDFStoreGraph].graph).asInstanceOf[Boolean]
    else
      false
  }

  def dup: RDFStoreGraph = new RDFStoreGraph(graph.dup())

  def size: Int = triples.length

}