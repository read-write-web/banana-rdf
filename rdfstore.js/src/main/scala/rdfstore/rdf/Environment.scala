package rdfstore.rdf

import scala.scalajs.js.annotation.JSName
import scala.scalajs.js

@JSName("RDFJSInterface.RDFEnvironment")
class Environment() extends Profile() {

  /**
   * @return a new blank node, updating the blank node counter for this environment
   */
  def createBlankNode(): BlankNode = ???


  /**
   * @param name either a IRI or a curie
   * @return a Named Node, with the curie resolved to an iri
   */
  def createNamedNode(name: String): NamedNode = ???

  /**
   * create a literal
   * @param value  the string literal
   * @param lang lang or null
   * @param datatype or null
   * @return the new Literal
   */
  def createLiteral(value: String, lang: String, datatype: String): Literal = ???

  def createTriple(subj: RDFNode, rel: NamedNode, obj: RDFNode): Triple = ???

  def createGraph(triples: js.Array[Triple]): Graph = ???

  //missing:
  //• createProfile
  //• createTermMap
  //• createPrefixMap
  //• createAction
}

@JSName("RDFJSInterface.Profile")
class Profile() extends js.Object {

  //todo: should this be var or val?
  val prefixes: UrisMap = ???

  //todo: should this be var or val?
  val terms: UrisMap = ???

  /**
   * import the terms and prefixes from other
   * @param other
   */
  def importProfile(other: Profile): Unit = ???

  /**
   *
   * @param toResolve
   * @return
   */
  def resolve(toResolve: String): String = ???


  def setDefaultPrefix(iri: String): Unit = ???

  /**
   * set default for terms
   * @param iri
   */
  def setDefaultVocabulary(iri: String): Unit = ???

  /**
   * set a term
   * @param term
   * @param iri
   */
  def setTerm(term: String, iri: String): Unit = ???

}
