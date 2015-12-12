package org.w3.banana.rdfstorew

import org.w3.banana.{Prefix, SparqlOps, VarNotFound}

import scala.util.{ Failure, Try, Success }

import scalajs.js

object RDFSparqlOps extends SparqlOps[RDFStore] {
  def SelectQuery(query: String): RDFStore#SelectQuery = query

  def ConstructQuery(query: String): RDFStore#ConstructQuery = query

  def AskQuery(query: String): RDFStore#AskQuery = query

  def UpdateQuery(query: String): RDFStore#UpdateQuery = query

  def Query(query: String): Try[RDFStore#Query] = Success(query)

  def fold[T](query: RDFStore#Query)(select: (RDFStore#SelectQuery) => T,
    construct: (RDFStore#ConstructQuery) => T,
    ask: RDFStore#AskQuery => T) =
    query match {
      case qs: RDFStore#SelectQuery => select(qs)
      case qc: RDFStore#ConstructQuery => construct(qc)
      case qa: RDFStore#AskQuery => ask(qa)
    }

  def getNode(solution: RDFStore#Solution, v: String): Try[RDFStore#Node] = {
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
                  RDFStore.ops.makeUri(node.asInstanceOf[js.Dynamic].selectDynamic("type").asInstanceOf[String])
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
                RDFStore.ops.makeLangTaggedLiteral(
                  node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String],
                  lang
                )
              } else {
                RDFStore.ops.makeLiteral(
                  node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String],
                  datatype
                )
              }
            }
            case "blank" => {
              RDFStore.ops.makeBNodeLabel(node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String])
            }
            case "uri" => {
              RDFStore.ops.makeUri(node.asInstanceOf[js.Dynamic].selectDynamic("value").asInstanceOf[String])
            }
            case _ => throw new Exception("Unknown solution type:'" + node.asInstanceOf[js.Dynamic].selectDynamic("token") + "'")
          }
          Success(namedNode)
        }
      }
      case _ => throw new Exception("SPARQL solution type not implemented yet")
    }
  }

  def varnames(solution: RDFStore#Solution): Set[String] = {
    var s = Set[String]()
    for (key <- js.Object.keys(solution.asInstanceOf[js.Dictionary[Any]]).iterator) { s = s + key }
    s
  }

  def solutionIterator(solutions: RDFStore#Solutions): Iterator[RDFStore#Solution] = solutions.toIterator



  override def parseSelect(query: String, prefixes: Seq[Prefix[RDFStore]]): Try[RDFStore#SelectQuery] = Success(SelectQuery(query))

  override def parseConstruct(query: String, prefixes: Seq[Prefix[RDFStore]]): Try[RDFStore#ConstructQuery] = Success(ConstructQuery(query))

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
  override def parseQuery(query: String, prefixes: Seq[Prefix[RDFStore]]): Try[RDFStore#Query] = Query(query)

  override def parseAsk(query: String, prefixes: Seq[Prefix[RDFStore]]): Try[RDFStore#AskQuery] = Success(AskQuery(query))

  override def parseUpdate(query: String, prefixes: Seq[Prefix[RDFStore]]): Try[RDFStore#UpdateQuery] = Success(UpdateQuery(query))
}