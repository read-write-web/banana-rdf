package rdfstore

import scala.scalajs.js

/**
 * Created by hjs on 27/12/2013.
 */
trait QueryFilters extends js.Object {

}

/**
  * Constructs used in the QueryFilters
  * see the function QueryFilters.runBuiltInCall
  * This is what is returned for each answer of a query
  */
trait TokenValue extends js.Object {
  /** can be either 'blank', 'literal' or 'uri' or `variable`*/
  val token: String = ???

  /**
   * if the token is 'literal' this can be the type as a uri, eg xsd:string
   * or the language
   */
  val `type`: String = ???

  /**
   * for literals this can be the language of the literal or "*"?
   */
  val lang: String = ???

  val value: String = ???

  val prefix: String = ???

  val suffix: String = ???
}