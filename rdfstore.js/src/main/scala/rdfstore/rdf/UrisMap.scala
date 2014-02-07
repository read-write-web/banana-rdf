package rdfstore.rdf

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
 * @todo (for JS) Note: there is no type distinction made here between uris and curies
 * ( which means that if by chance urns start with a prefix
 * or if someone uses http as a namespace, then uris could be turned into weird
 * monsters ). This could be a vector of attack.
 */
@JSName("RDFJSInterface.UrisMap")
class UrisMap() extends js.Object {

  /**
   *
   * @return the full namespace -> iri map
   */
  def values: js.Dictionary = ???

  /**
   * @param nameSpace
   * @return the iri for that namespace or undefined if none exists
   * @throws exception if namespace contains a whitespace
   */
  def get(nameSpace: String): String = ???

  /**
   * remove an prefix association
   * @param nameSpace
   * @throws exception if namespace contains a whitespace
   */
  def remove(nameSpace: String): Unit = ???

  /**
   * set a new namespace
   * @param nameSpace
   * @param iri
   * @throws exception if namespace contains a whitespace
   */
  def set(nameSpace: String, iri: String): Unit = ???

  /**
   * Set the default namespace
   * @param iri
   */
  def setDefault(iri: String): Unit = ???

  /**
   *
   * Add all namspaces->iri mappings given in the dictionary
   * @param dict a java object considered as a dictionary
   * @param overRide if false do not override the current settings
   * @todo: (for JS) this method does not specify if an override has failed. seems worrying
   */
  def addAll(dict: js.Dictionary, overRide: js.Boolean): Unit = ???

  /**
   * resolve a curie  iri to its full uri
   * @param curie  string in the form namespace:name
   * @return full iri or null if default namespace is not set or no mappings exist
   */
  def resolve(curie: String): String = ???

  /**
   *
   * @param iri
   * @return the same iri or the appropriate curie
   */
  def shrink(iri: String): String = ???

}
