package rdfstore

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName
import rdfstore.rdf._

@JSName("rdfstore")
object Store extends js.Object {

  /**
   * Type for connection callback
   * First argument is a true if a WebWorker was used,
   * The second argument is the Store to be used
   */
  type ConnectCallback = (Boolean, Store) => Unit
  type CreateCallback = js.Function1[Store,Unit]

  val VERSION = ???

  /**
   * Connect to store.
   * This will attempt to create a store in a WebWorkers thread.
   *
   * This will try to create a worker and will return a connection object providing the same interface of the store object.
   * If the creation of the worker fails, because webworkers support is not enabled in the platform/browser,
   * a regular store object will be returned instead.
   *
   * Web worker threads execute in the browser in a very restrictive environment due to security
   * reasons. WebWorkers for example, cannot access the local storage API. As a consequence, workers
   * cannot be used with the persistent version of the store. These restrictions are not
   * present in the Node.js version.
   *
   * @param rdfStoreJSLocation location of JS implementing the store
   * @param properties
   * @param callback function executed on completion.
   *
   * @return
   */

  def connect(rdfStoreJSLocation: String, properties: js.Dictionary, callback: ConnectCallback ): Unit = ???

  /**
   * same as connect with three variables but only to be used with node.js
   * <ul>
   *   <li>rdfstoreLocation: set to <pre>$dirname</pre>
   *   <li>properties: empty
   * </ul>
   */
  def connect(callback: ConnectCallback): Unit = ???

  /**
   * same as connect with three variables but only to be used with node.js
   * properties set to empty.
   */
  def connect(rdfStoreJSLocation: String, callback: ConnectCallback): Unit = ???

  /**
   * same as connect with three variables but only to be used with node.js
   * rdfstoreLocation is set to <pre>$dirname/index.js</pre>
   */
  def connect(properties: js.Dictionary, callback: ConnectCallback): Unit = ???


  /**
   * Create a new Store
   *
   * @param callback Callback when the store is created
   * @param properties to initialise the store
   * @return the new Store
   */
  def create(callback: CreateCallback, properties: js.Dictionary): Store = ???

  /**
   * Create a new Store
   *
   * @param callback
   * @return the new Store
   */
  def create(callback: CreateCallback): Store = ???


  /**
   * @return create a new Store with default properties and no callback
   */
  def create(): Store =  ???


}


/**
 * Created by hjs on 26/12/2013.
 */
trait Store extends js.Object {

   val rdf: Environment = ???

  // ignore the constructors for the moment. We can use the object Store above instead

  /**
   * registers a custom function to transform a list of TokenValue answers into another TokenValue
   *
   * Registers a new function with an associated name that can
   * be invoked as 'custom:fn_name(arg1,arg2,...,argn)' inside
   * a SPARQL query.
   * <br/>
   * The registered function will receive two arguments, an
   * instance of the store's query filters engine and a list
   * with the arguments received by the function in the SPARQL query.
   * <br/>
   * The function must return a single token value that can
   * consist in a literal value or an URI.
   * <br/>
   * The following is an example literal value:
   * {token: 'literal', type:"http://www.w3.org/2001/XMLSchema#integer", value:'3'}
   * This is an example URI value:
   * {token: 'uri', value:'http://test.com/my_uri'}
   * <br/>
   * The query filters engine can be used to perform common operations
   * on the input values.
   *
   * An error can be return using the 'ebvError' function of the engine.
   * True and false values can be built directly using the 'ebvTrue' and
   * 'ebvFalse' functions.
   *
   * A complete reference of the available functions can be found in the
   * documentation or source code of the QueryFilters module.
   *
   * @param name
   * @param callback
   * @return  this store ( test it )
   */
   def registerCustomFunction(name: String, callback: js.Function2[QueryFilters, js.Array[TokenValue],TokenValue]): Store  = ???


  /**
   * Removes an event listener associated to a certain pattern.
   * The function passed as an argument to <code>subscribe</code> must be
   * passed as an argument.
   *
   * @param function the function that was passed to subscribe
   */
   def unsubscribe(function: Function2[js.Object,js.Object,Unit]): Unit = ???


  /**
   * Retrieves all the quads belonging to a certain node
   * in the store as a RDF JS Interface Graph object containing
   * the collection of triples whose subject is the provided
   * node URI.<br/>
   * <br/>
   * The function accepts as mandatory parameters the node URI and
   * a callback unction that will receive a success notification and the returned node.<br/>
   * <br/>
   * Optionally, the URI of the graph where the node is contained
   * can also be passed as the first argument. <br/>
   * <br/>
   * If no graph is specified, the node will be looked into the
   * default graph.<br/>
   *
   * @arguments
   * @param nodeURI URI of the node to look for
   * @param graphName If this parameter is missing, the node will be looked into the default graph
   * @param callback
   */
   def node(nodeUri: String, graphName: String, callback: js.Function2[js.Boolean, js.Object, Unit] ): Unit = ???

  /**
   * Inserts a RDF JS Interface API <code>Graph</code> object into the store.
   * The function receives a mandatory <code>Graph</code> object whose triples
   * will be inserted. Optionally, a URI string for a graph and a
   * callback function can be passed as arguments.<br/>
   * <br/>
   * If no graph URI is specified, triples will be inserted into the
   * default graph.<br/>
   * <br/>
   * If the callback function is specified, it will be invoked when all the
   * triples had been inserted into the store.<br/>
   *
   * @param graph <code>Graph</code> object
   * @param graphName URI of the graph where the triples will be inserted.
   *                  If it is missing, triples will be inserted in the default graph
   * @param callback A callback function that will be invoked with on failure
   *                 with an error message, or on success with an array of { subject, predicate, object, graph }
   */
  def insert(graph: Graph, graphName: String, callback: js.Function2[js.Boolean, js.Object, Unit]): Unit = ???

  /**
   * Insert triples from graph into default graph
   * @param graph triples to be inserted
   */
  def insert(graph: Graph): Unit = ???

  /**
   * Insert graph into graph with given name
   * @param graph
   * @param graphName
   */
  def insert(graph: Graph, graphName: String): Unit = ???

  /**
   * Load triples into a graph in the store. Data can be passed directly to the method
   * or a remote URI speifying where the data is located can be used.<br/>
   *<br/>
   * If the data is passed directly to the load function, the media type stating the format
   * of the data must also be passed to the function.<br/>
   *<br/>
   * If an URI is passed as a parameter, the store will attempt to perform content negotiation
   * with the remote server and get a representation for the RDF data matching one of the
   * the RDF parsers registered in the store. In this case, the media type parameter must be
   * set to the <code>'remote'</code> value.<br/>
   *<br/>
   * An additional URI for the graph where the parsed data will be loaded and a callback function
   * can be also passed as parameters. If no graph is specified, triples will be loaded in the
   * default graph.<br/>
   *<br/>
   * By default loading data will not trigger notification through the events API. If events needs to
   * be trigger, the functio <code>setBatchLoadEvents</code> must be invoked with a true parameter.
   *
   * @param  mediaType Media type (application/json, text/n3...) of the data to be parsed or the value <code>'remote'</code> if a URI for the data is passed instead
   * @param  data RDF data to be parsed and loaded or an URI where the data will be retrieved after performing content negotiation
   * @param  graphName Graph where the parsed triples will be inserted. If it is not specified, triples will be loaded in the default graph
   * @param  callback that will be invoked with a success notification and the number of triples loaded.
   */
  def load(mediaType: String, data: String, graphName: String, callback: js.Function2[js.Boolean,js.Number, Unit])


  /**
   * Returns the URI of all the graphs currently contained
   * in the store
   *
   * @param callback function that will receive a success notification and the array of graph URIs
   */
  def registeredGraphs(callback: js.Function2[js.Boolean,js.Array[NamedNode],Unit])


  /**
   * Removes the triples in a RDF JS Interface API <code>Graph</code> object from the store.
   * The function receives a mandatory <code>Graph</code> object whose triples
   * will be removed. Optionally, a URI string for a graph and a
   * callback function can be passed as arguments.<br/>
   * <br/>
   * If no graph URI is specified, triples will be removed from the
   * default graph.<br/>
   * <br/>
   * If the callback function is specified, it will be invoked when all the
   * triples had been removed from the store.
   *
   * @param toBeRemoved Graph of triples to be removed
   * @param fromGraphURI URI of the graph where the triples will be removed from. If it is missing, triples will be removed from the default graph
   * @param callback function that will be invoked with a success notification
   */
  def delete(toBeRemoved: Graph, fromGraphURI: String, callback: js.Function2[js.Boolean, js.Object, Unit]): Unit = ???

  def delete(toBeRemoved: Graph, callback: js.Function2[js.Boolean, js.Object, Unit]): Unit = ???

  def delete(toBeRemoved: Graph): Unit = ???


  /**
   * Associates an event listener function to a node URI. Every time the collection
   * of triples whose subject is the specified node URI changes, because an
   * insertion or deletion, the provided callback function will be invoked
   * receiving as a parameter a RDF JS Interface Graph object with the new
   * collection of triples.<br/>
   * <br/>
   * The function accepts two mandatory arguments, the URI of the node to observe
   * and the function that will receive the event notifications. An optional
   * third parameter, consisting of a callback function, can be passed and will be invoked
   * once the store had correctly configured the event listener.<br/>
   *<br/>
   * LOAD queries, batch loading data into the store, do not
   * trigger events by default. If you wish to be notified
   * by changes triggered by this kind of queries, invoke
   * the *setBatchLoadEvents* function with a true argument.<br/>
   *<br/>
   * The event listener function can be removed using the stopObservingNode function.
   *
   * @param nodeUri URI of the node to observe
   * @param graphName  name of graph to listen to
   * @param  eventListener Function that will be notified with the events
   */
  def startObservingNode(nodeUri: String, graphName: String, eventListener: js.Function1[Graph, Unit]): Unit = ???

  /**
   * Same as above but the graphUri is the default graph
   * @param nodeUri
   * @param eventListener
   */
  def startObservingNode(nodeUri: String, eventListener: js.Function1[Graph, Unit]): Unit = ???

  /**
   * Removes a callback function associated to a node.<br/>
   * The event listener function object must be passed as an argument.<br/>
   *
   * @param eventListener The event listener function to remove, the same passed as an argument to startObservingNode
   */
  def stopObservingNode(eventListener: js.Function1[Graph, Unit]): Unit = ???

  /**
   * Associates an event listener to a pattern expressed as the
   * subject, predicate, object and graph string parameters passed
   * to the function. To match any value in that position, a <code>null</code>
   * value can be passed as an argument. e.g. <code>subscribe(null, null, null, g, cb)</code>,
   * will be notified with any change in the g graph.<br/>
   * The graph component of the pattern does not support a <code>null</code> value.<br/>
   *<br/>
   * Results will be notified as an Array of RDF JS Interface
   * <code>Triple</code> objects.<br/>
   *<br/>
   * LOAD queries, batch loading data into the store, do not
   * trigger events by default. If you wish to be notified
   * by changes triggered by this kind of queries, invoke
   * the <code>setBatchLoadEvents</code> function with a true argument.
   *
   * @param s subject or null for any subject
   * @param p predicate or null for any predicate
   * @param o object or null for any object
   * @param graphName graph name ( null refers to the default graph - not all graphs )
   * @param eventFun listener function that will be notified when a change occurs
   */
  def subscribe(s: String, p: String, o: String, graphName: String, eventFun: js.Function2[js.String,js.Array[Triple],Unit] ): Unit = ???

  /**
   * Removes all the triples stored in a graph.
   *
   * The URI of the graph and a callback function can be
   * optionally passed as parameters.<br/>
   * <br/>
   * If no graph URI is specified, all triples in the
   * default graph will be removed.
   *
   * @param graphName the URI of the graph the triples must be removed from
   * @param callback a function that will be invoked with a success notification
   */
   def clear(graphName: String, callback: js.Function1[js.Boolean,Unit]): Unit = ???

  /**
   * clear the default graph with no callback
   */
   def clear()

  /**
   * clear the default graph with given callback
   */
  def clear(callback: js.Function1[js.Boolean,Unit]): Unit = ???

  /**
   * Boolean value determining if loading RDF must produce
   * triple add events and fire callbacks.<br/>
   * Default value is false.
   *
   * @param bool mustFireEvents true/false value.
   */
  def setBatchLoadEvents(bool: js.Boolean): Unit = ???


}


