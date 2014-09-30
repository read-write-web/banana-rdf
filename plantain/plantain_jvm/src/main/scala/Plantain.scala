package org.w3.banana.plantain

import akka.http.model.Uri

import org.openrdf.query.BindingSet
import org.openrdf.query.parser._

trait Plantain extends org.w3.banana.plantain.generic.Plantain[Uri] {
  // types related to Sparql
  type Query = ParsedQuery
  type SelectQuery = ParsedTupleQuery
  type ConstructQuery = ParsedGraphQuery
  type AskQuery = ParsedBooleanQuery
  // FIXME added just to avoid compilation error
  type UpdateQuery = SesameParseUpdate
  type Solution = BindingSet
  // instead of TupleQueryResult so that it's eager instead of lazy
  type Solutions = BoundSolutions
}

object Plantain extends PlantainModule

case class BoundSolutions(iterator: Iterator[BindingSet], bindings: List[String])
case class SesameParseUpdate(query: String)

