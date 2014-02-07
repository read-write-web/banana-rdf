package rdfstore.rdf

import scala.scalajs.js.annotation.JSName
import scala.scalajs.js

@JSName("RDFJSInterface.RDFNode")
trait RDFNode extends js.Object {

}

/**
 * Blank nodes should be created via the Environment,
 * which keeps track of node ids
 */
@JSName("RDFJSInterface.BlankNode")
trait BlankNode extends RDFNode {
  def toNT(): String = ???
}

@JSName("RDFJSInterface.Literal")
class Literal protected () extends RDFNode {
  def this(value: String, lang: String, dataType: String) = this()
  def toNT(): String = ???
}

/**
* The class of things that are named.
*
* Other projects tend to call this a URI, but a URI is an identifier,
* whereas this (correctly) suggests that it is the thing identified that
* takes precedence.
*
* ( A better name would be something that tried to name the relation between
* the name and the thing - perhaps NamedReference, BNodeReference )
*/
@JSName("RDFJSInterface.NamedNode")
class NamedNode protected () extends RDFNode {
  def this(value: String) = this()
  def toNT(): String = ???
}

@JSName("RDFJSInterface.Literal")
class Triple protected() extends js.Object {
  def this(subject: RDFNode, relation: RDFNode, obj: RDFNode)  = this()
  val subject: RDFNode = ???
  val relation: RDFNode = ???
  val `object`: RDFNode = ???
}

trait Quad extends js.Object {
  val subject: String = ???
  val relation: String = ???
  val `object`: String = ???
  val graph: String = ???
}



/**
 * Graph object containing a set of Triples
 */
class Graph() extends js.Object {

  /**
   * Add a triple to the store after running it iteratively through all the stored actions
   * todo
   * @param triple
   * @return the final triple
   */
  def add(triple: Triple): Graph = ???

  /**
   * add an action to the Graph
   *
   * @param action an action that presumably should not transform a triple semantically.
   * @param run if true, run this action over all triples in the graph
   * @return this
   */
  def addAction(action: js.Function1[Triple,Triple], run: Boolean): Graph = ???

  /**
   * Add all triples of the current graph to this graph
   * @param graph
   * @return this
   */
  def addAll(graph: Graph): Graph = ???

  /**
   *
   * @param triple to be removed from graph
   * @return this
   */
  def remove(triple: Triple): Graph = ???

  def toArray: js.Array[Triple]  = ???

  /**
   *
   * @param condition note: the graph is this.
   * @return true if some triple matches condition
   */
  def some(condition: (Triple,Graph) => Boolean): Boolean = ???

  /**
   *
   * @param condition note: the graph is this.
   * @return true if every triple matches
   */
  def every(condition: (Triple,Graph) => Boolean): Boolean = ???

  /**
   *
   * @param condition note: the graph is this.
   * @return a graph where every triple matches condition
   */
  def filter(condition: (Triple,Graph) => Boolean ): Graph = ???

  /**
   * apply the given action to each triple that matches
   * @param action
   */
  def forEach(action: (Triple, Graph) => Unit):  Unit = ???

  /**
   *
   * merge this graph with that
   * @param that
   * @note there is no blank node remapping here. This will only work if blank nodes are unique
   * across graphs! So parsers have to also alocate new blank nodes per graph created.
   */
  def merge(that: Graph) : Graph = ???

  /**
   *
   * @param subj subject to be matched
   * @param rel relation to be matched
   * @param obj object to be matched
   * @param limit max amount of triples to search for
   * @return return a graph of triples where each of the subj, rel, obj matches. null matches anything.
   */
  @JSName("match")
  def matching(subj: RDFNode, rel: RDFNode, obj: RDFNode, limit: Integer): Graph = ???

  /**
   *
   * @param subj subject to be matched
   * @param rel relation to be matched
   * @param obj object to be matched
   * @return this, with all the matching Triples removed ( null matches any triple )
   */
  def removeMatches(subj: RDFNode, rel: RDFNode, obj: RDFNode): Graph = ???

  /**
   * @return a string representation of the graph in NTriples format
   */
  def toNT(): String = ???


}