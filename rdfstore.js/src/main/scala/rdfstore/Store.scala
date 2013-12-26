package rdfstore

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

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


}
