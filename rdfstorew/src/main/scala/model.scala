package org.w3.banana.rdfstorew

import scala.scalajs.js


class RDFStoreRDFNode(node:js.Dynamic) {

  val jsNode = node

  val interfaceName:js.String = node.interfaceName.asInstanceOf[String]
  val attributes:js.Array[js.String] = node.attributes.asInstanceOf[js.Array[String]]

  override def equals(other:Any) : Boolean = {
    if(other.isInstanceOf[RDFStoreRDFNode]) {
      jsNode.applyDynamic("equals")(other.asInstanceOf[RDFStoreRDFNode].jsNode).asInstanceOf[Boolean]
    } else {
      false
    }
  }

  override def toString:js.String = node.toString().asInstanceOf[js.String]

  def toNT:js.String = node.toNT().asInstanceOf[js.String]

  def valueOf:js.String = node.valueOf().asInstanceOf[js.String]
}

class RDFStoreBlankNode(node:js.Dynamic) extends  RDFStoreRDFNode(node) {

  val bnodeId:js.String = node.bnodeId.asInstanceOf[js.String]

}

class RDFStoreLiteral(node:js.Dynamic) extends  RDFStoreRDFNode(node) {

  val nominalValue:js.String = if(node.nominalValue.isInstanceOf[js.String]) { node.nominalValue.asInstanceOf[js.String] } else { null }
  val datatype:js.String = if(node.datatype.isInstanceOf[js.String]) { node.datatype.asInstanceOf[js.String] } else { null}
  val language:js.String = if(node.language.isInstanceOf[js.String]) { node.language.asInstanceOf[js.String] } else { null }

}


class RDFStoreNamedNode(node:js.Dynamic) extends  RDFStoreRDFNode(node) {

  val nominalValue:js.String = node.nominalValue.asInstanceOf[js.String]

}


class RDFStoreTriple(node:js.Dynamic) {
  val triple = node

  val subject:RDFStoreRDFNode = {
    val nodeSubject = new RDFStoreRDFNode(node.subject)
    nodeSubject.interfaceName match {
      case "BlankNode" =>
        new RDFStoreBlankNode(node.subject)
      case "Literal" =>
        new RDFStoreLiteral(node.subject)
      case "NamedNode" =>
        new RDFStoreNamedNode(node.subject)
      case _ =>
        throw new Exception("Unknown RDF JS interface "+nodeSubject.interfaceName)
    }
  }

  val predicate:RDFStoreNamedNode = new RDFStoreNamedNode(node.predicate)

  val objectt:RDFStoreRDFNode = {
    val nodeObject = new RDFStoreRDFNode(node.selectDynamic("object"))
    nodeObject.interfaceName match {
      case "BlankNode" =>
        new RDFStoreBlankNode(node.subject)
      case "Literal" =>
        new RDFStoreLiteral(node.subject)
      case "NamedNode" =>
        new RDFStoreNamedNode(node.subject)
      case _ =>
        throw new Exception("Unknown RDF JS interface "+nodeObject.interfaceName)
    }
  }


  override def equals(other:Any) : Boolean = {
    if(other.isInstanceOf[RDFStoreTriple])
      triple.asInstanceOf[js.Dynamic].applyDynamic("equals")(other.asInstanceOf[RDFStoreTriple].triple).asInstanceOf[Boolean]
    else
      false
  }

  override def toString:js.String = node.toString().asInstanceOf[js.String]
}

class RDFStoreGraph(node:js.Dynamic) {
  val graph = node

  def triples:js.Array[RDFStoreTriple] = {
    node.triples.asInstanceOf[js.Array[js.Dynamic]].map((t:js.Dynamic) => new RDFStoreTriple(t))
  }

  def add(triple:RDFStoreTriple) : RDFStoreGraph = {
    node.add(triple.triple)
    this
  }

  def merge(other:RDFStoreGraph) : RDFStoreGraph = new RDFStoreGraph(graph.merge(other.graph))

  override def equals(other:Any) : Boolean = {
    if(other.isInstanceOf[RDFStoreGraph])
      graph.asInstanceOf[js.Dynamic].applyDynamic("equals")(other.asInstanceOf[RDFStoreGraph].graph).asInstanceOf[Boolean]
    else
      false
  }

}