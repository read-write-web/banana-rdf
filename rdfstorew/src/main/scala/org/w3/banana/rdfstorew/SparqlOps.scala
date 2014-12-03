package org.w3.banana.rdfstorew

import org.w3.banana.{Prefix, SparqlOps, VarNotFound}

import scala.util.{ Failure, Try, Success }

import scalajs.js

object RDFSparqlOps extends SparqlOps[JSStore] {
  def SelectQuery(query: String): JSStore#SelectQuery = query

  def ConstructQuery(query: String): JSStore#ConstructQuery = query

  def AskQuery(query: String): JSStore#AskQuery = query

  def UpdateQuery(query: String): JSStore#UpdateQuery = query

  def Query(query: String): Try[JSStore#Query] = Success(query)

  def fold[T](query: JSStore#Query)(select: (JSStore#SelectQuery) => T,
    construct: (JSStore#ConstructQuery) => T,
    ask: JSStore#AskQuery => T) =
    query match {
      case qs: JSStore#SelectQuery => select(qs)
      case qc: JSStore#ConstructQuery => construct(qc)
      case qa: JSStore#AskQuery => ask(qa)
    }

  def getNode(solution: JSStore#Solution, v: String): Try[JSStore#Node] = {
    solution match {
      case s: SPARQLSolutionTuple => {
        var node = s(v)
        if (node == null)
          Failure(VarNotFound("var " + v + " not found in BindingSet " + solution.toString))
        else {
          val namedNode = node.asInstanceOf[js.Dynamic].selectDynamic("token").asInstanceOf[String] match {
            case "literal" => {
              val datatype: RDFStoreNamedNode = {
                if (node.asInstanceOf[js.Dynamic].selectDynamic("type").isInstanceOf[Unit] ||
                  node.asInstanceOf[js.Dynamic].selectDynamic("type") == null) {
                  null
                } else {
                  JSStore.ops.makeUri(node.asInstanceOf[js.Dynamic].selectDynamic("type").asInstanceOf[String])
                }
              }

              val lang: String = {
                if (node.asInstanceOf[js.Dynamic].selectDynamic("lang").isInstanceOf[Unit] ||
                  node.asInstanceOf[js.Dynamic].selectDynamic("lang") == null) {
                  null
                } else {
                  node.asInstanceOf[js.Dynamic].selectDynamic("lang").asInstanceOf[String]
                }
              }

              if (lang != null) {
                JSStore.ops.makeLangTaggedLiteral(
                  node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String],
                  lang
                )
              } else {
                JSStore.ops.makeLiteral(
                  node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String],
                  datatype
                )
              }
            }
            case "blank" => {
              JSStore.ops.makeBNodeLabel(node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String])
            }
            case "uri" => {
              JSStore.ops.makeUri(node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String])
            }
            case _ => throw new Exception("Unknown solution type:'" + node.asInstanceOf[js.Dynamic].selectDynamic("token") + "'")
          }
          Success(namedNode)
        }
      }
      case _ => throw new Exception("SPARQL solution type not implemented yet")
    }
  }

  def varnames(solution: JSStore#Solution): Set[String] = {
    var s = Set[String]()
    for (key <- js.Object.keys(solution.asInstanceOf[js.Dictionary[Any]]).iterator) { s = s + key }
    s
  }

  def solutionIterator(solutions: JSStore#Solutions): Iterator[JSStore#Solution] = solutions.toIterator



  override def parseSelect(query: String, prefixes: Seq[Prefix[JSStore]]): Try[JSStore#SelectQuery] = Success(SelectQuery(query))

  override def parseConstruct(query: String, prefixes: Seq[Prefix[JSStore]]): Try[JSStore#ConstructQuery] = Success(ConstructQuery(query))

  /**
   * A general query constructor.
   *
   * When this is used it is usually because the query type is not
   * known in advance, ( as when a query is received over the
   * internet). As a result the response is a validation, as the
   * query may not have been tested for validity.
   *
   * @param query a Sparql query
   * @return A validation containing the Query
   */
  override def parseQuery(query: String, prefixes: Seq[Prefix[JSStore]]): Try[JSStore#Query] = Query(query)

  override def parseAsk(query: String, prefixes: Seq[Prefix[JSStore]]): Try[JSStore#AskQuery] = Success(AskQuery(query))

  override def parseUpdate(query: String, prefixes: Seq[Prefix[JSStore]]): Try[JSStore#UpdateQuery] = Success(UpdateQuery(query))
}