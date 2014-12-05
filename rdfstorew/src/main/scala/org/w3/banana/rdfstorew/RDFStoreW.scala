package org.w3.banana.rdfstorew

import org.w3.banana.{RDFStore, RDFOps, SparqlUpdate}

import scala.concurrent._
import scala.language.postfixOps
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.util.Try
import org.w3.banana.rdfstore.rjs


class RDFStoreW()(implicit ops: RDFOps[JSStore]) extends RDFStore[JSStore, Future, rjs.Store] with SparqlUpdate[JSStore, Future, rjs.Store] {

  def executeQuery(store: rjs.Store, sparql: String): Future[Any] = {
    val promise = Promise[Any]

    store.execute(sparql,  (success: Boolean, res: js.Any) =>
      if (success) {
        promise.success(res)
      } else {
        promise.failure(new Exception("Error running query: " + res))
      }

    )

    promise.future
  }

  def executeQueryUnit(store: rjs.Store, sparql: String): Future[Unit] = {
    val promise = Promise[Unit]
    def cb0(success: Boolean, res: Any): Promise[Unit] =
      if (success) {
        promise.success()
      } else {
        promise.failure(new Exception("Error running query: " + res))
      }

    store.executeUnit(sparql, cb0 _)

    promise.future
  }

  def bindQuery(query: String, bindings: Map[String, RDFStoreRDFNode]): String = {
    var tmp = query
    for ((name, node) <- bindings) {
      tmp = tmp.replaceAll("?" + name, node.jsNode.toNT().asInstanceOf[js.String])
    }
    tmp
  }


  /* Transactor */

  // RDFStore-js doesn't support transactions

  /** Evaluates `body` in a read transaction. */
  override def r[T](store: rjs.Store, body: => T): Try[T] = Try {
    try {
      val result = body
      result
    }
  }

  /** Evaluates `body` in a read/write transaction. */
  override def rw[T](store: rjs.Store, body: => T): Try[T] = Try {
    try {
      val result = body
      result
    }
  }


  /** Executes a Construct query. */
  override def executeConstruct(store: rjs.Store, query: JSStore#ConstructQuery, bindings: Map[String, JSStore#Node]): Future[JSStore#Graph] = {
    executeQuery(store, bindQuery(query, bindings)) map {
      g => {
        new RDFStoreGraph(g.asInstanceOf[rjs.Graph])
      }
    }
  }

  /** Executes a Select query. */
  override def executeSelect(store: rjs.Store, query: JSStore#SelectQuery, bindings: Map[String, JSStore#Node]): Future[JSStore#Solutions] = {
    executeQuery(store, bindQuery(query, bindings)) map {
      solutions =>
        solutions.asInstanceOf[js.Array[js.Dynamic]].map[SPARQLSolutionTuple] {
          (o: js.Dynamic) => new SPARQLSolutionTuple(o.asInstanceOf[js.Dictionary[js.Any]])
        }.toArray
    }
  }

  /** Executes a Ask query. */
  override def executeAsk(store: rjs.Store, query: JSStore#AskQuery, bindings: Map[String, JSStore#Node]): Future[Boolean] = {
    executeQuery(store, bindQuery(query, bindings)) map {
      b => {
        b.asInstanceOf[Boolean]
      }
    }
  }

  override def executeUpdate(store: rjs.Store, query: JSStore#UpdateQuery, bindings: Map[String, JSStore#Node]): Future[rjs.Store] = {
    executeQueryUnit(store, bindQuery(query, bindings)) map identity
    Future.successful(store)
  }

  /**
   * To the graph at `uri`, removes the matching triples
   */
  override def removeTriples(store: rjs.Store, graph: JSStore#URI, triples: Iterable[(JSStore#NodeMatch, JSStore#NodeMatch, JSStore#NodeMatch)]): Future[Unit] = {
    val promise = Promise[Unit]
    def callback(success: Boolean, res: Any): Promise[Unit] =
      if (success) {
        promise.success()
      } else {
        promise.failure(new Exception("Error deleting triples into the store: " + res))
      }


    val graphToRemove = ops.emptyGraph
    for(triple <- triples) {
      triple match {
        case (PlainNode(s), PlainNode(p:RDFStoreNamedNode), PlainNode(o)) =>
          val triple = ops.makeTriple(s,p,o)
          graphToRemove.add(triple)
      }
    }


    if (graph == null) {
      store.delete(graphToRemove.graph, callback _)
    } else {
      store.delete(graphToRemove.graph, graph.valueOf, callback _)
    }

    promise.future
  }

  /**
   * To the graph at `uri`, appends the content of `graph`. If there was
   * no previous graph, this would create it.
   */
  override def appendToGraph(store: rjs.Store, graph: JSStore#URI, triples: RDFStoreGraph): Future[Unit] = {
    val promise = Promise[Unit]
    def cb2(success: Boolean, res: Any): Promise[Unit] =
        if (success) {
          promise.success()
        } else {
          promise.failure(new Exception("Error inserting triples into the store: " + res))
        }


    if (graph == null) {
      store.insert(triples.graph, cb2 _)
    } else {
      store.insert(triples.graph, graph.valueOf, cb2 _)
    }

    promise.future
  }

  /** Removes the graph at `uri`. */
  override def removeGraph(store: rjs.Store, graph: JSStore#URI): Future[Unit] = {
    val promise = Promise[Unit]
    val cb3 = (success: Boolean, res: js.Any) =>
        if (success) {
          promise.success()
        } else {
          promise.failure(new Exception("Error cleaning graph from the store store: " + res))
        }


    if (graph == null) {
      store.clear(cb3)
    } else {
      store.clear(graph.valueOf, cb3)
    }

    promise.future
  }


  /** Gets the graph at `uri`. */
  override def getGraph(store: rjs.Store, uri: RDFStoreNamedNode): Future[JSStore#Graph] = {
    val promise = Promise[JSStore#Graph]
    store.graph(uri.valueOf, (success: Boolean, res: js.Any) =>
        if (success) {
          promise.success(new RDFStoreGraph(res.asInstanceOf[rjs.Graph]))
        } else {
          promise.failure(new Exception("Error exporting data as a RDF graph"))
        } )
    promise.future
  }

}

object RDFStoreJS {
  def rdfStoreW(implicit ops: RDFOps[JSStore]) = new RDFStoreW()(JSStore.ops)
}

class RDFStoreJS(options: Map[String, Any]) {

  val rdfstorejs: rjs.Store = makeRDFStoreJS(options)

//  val rdf = rdfstorejs.asInstanceOf[js.Dynamic].selectDynamic("rdf")
//
//  val rdf_api = rdfstorejs.asInstanceOf[js.Dynamic].selectDynamic("rdf").selectDynamic("api")


//  def apply(options: Map[String, Any]): RDFStoreW = {
//    rdfstorejs = makeRDFStoreJS(options)
//
//  }

  def makeRDFStoreJS(options: Map[String, Any]): rjs.Store = {
    val dic = options.foldLeft[js.Dictionary[Any]](js.Dictionary())({
      case (acc, (key, value)) =>
        acc.update(key, value); acc
    })

    val promise = Promise[Unit]

    // hack for Rhino/browser execution
    val rdfstore = if (global.window != null) {
      global.window.rdfstore
    } else {
      global.rdfstore
    }
    println("store="+rdfstore)
    var newRdfstorejs:rjs.Store = null
    rdfstore.applyDynamic("create")(dic, (store: rjs.Store) => promise.success{
      newRdfstorejs = store
    })

    // always succeeds because 'create' is synchronous
    promise.future.value.get.get

    newRdfstorejs
  }
}