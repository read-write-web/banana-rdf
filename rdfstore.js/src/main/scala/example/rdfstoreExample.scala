package example

import scala.scalajs.js
import rdfstore.Store
import rdfstore.rdf.{Quad, Graph, NamedNode, Triple}

/**
 * Created by hjs on 26/12/2013.
 */
object rdfstoreExample {
  val console = js.Dynamic.global.console

  def main(): Unit = {
    console.log("in main()")

    //1. Create Store
    val store = Store.create { test _ }
    val env = store.rdf

    console.log()
    console.log("created store")
    console.log(store)

    val henry = env.createNamedNode("http://bblfish.net/#hjs")
    val henryDomain = "http://bblfish.net/"
    console.log("Create a graph and store it under "+henryDomain)
    val t1 = env.createTriple(henry,env.createNamedNode("foaf:name"),env.createLiteral("Henry","en",null))
    console.log(s"triple to be added is t1=$t1")
    val g = env.createGraph(js.Array(t1))
    store.insert(g,"http://bblfish.net/",(bool: js.Boolean,obj: js.Object)=> {
      console.log(s"inserted succeeded of t1: $bool");
      console.log(obj);
      ()})

    console.log()
    console.log(s"subscribe to events on the <$henryDomain> graph")
    //create a subscription to events on that graph
    val eventfun: js.Function2[js.String,js.Array[Triple],Unit] = (a: js.String,b: js.Array[Triple])=>{
      console.log("eventfun: received event for subscription")
      console.log(a)
      console.log(b)
      ()
    }
    store.subscribe(null,null,null,henryDomain,eventfun)


    console.log()
    console.log(s"subscribe to event notifications on <$henry> in <$henryDomain>")
    //try a node observation subscription
    val eventfun2: js.Function1[Graph,Unit] = (g: Graph) =>{
      console.log("eventfun2: received event for subscription with graph")
      console.log(g)
      ()
    }
    store.startObservingNode(henry.toString,henryDomain,eventfun2)
    store.setBatchLoadEvents(true)

    console.log()
    val t2 = env.createTriple(henry,env.createNamedNode("foaf:knows"),henry)
    val g2 = env.createGraph(js.Array(t2))
    console.log(s"insert a new triple t2=$t2")

    store.insert(g2,"http://bblfish.net/",(bool: js.Boolean,obj: js.Object)=> {
      console.log(s"inserted g2 succeeded $bool");
      console.log(obj);
      ()})

    console.log()
    val t3 = env.createTriple(env.createBlankNode(),env.createNamedNode("foaf:name"),env.createLiteral("Joe","en",null))
    console.log("now inserting new triple which does not mention henry and so should not trigger the node observation:"+t3)
    val g3 = env.createGraph(js.Array(t3))
    store.insert(g3,"http://bblfish.net/",(bool: js.Boolean,obj: js.Object)=> {
      console.log(s"insert g3 succeeded $bool . Should not trigger eventfun2 ");
      console.log(obj);
      ()})

    console.log(s"delete $t3 from graph <$henryDomain>")
    store.delete(g3,henryDomain,(succ: js.Boolean,obj: js.Object) => {
      console.log(s"deleted $succ of $t3. obj=") ;
      console.log(obj)
      ()}
    )

    val timblDoc = "http://www.w3.org/People/Berners-Lee/card"
    console.log(s"now load <$timblDoc>")
    store.load("remote",timblDoc,timblDoc,(succ:js.Boolean,res: js.Number)=>{
      console.log(s"loaded <$timblDoc> was a success = $succ . received $res quads")
      store.registeredGraphs((s: js.Boolean,registeredGraphs: js.Array[NamedNode])=>{
        console.log("call to registeredGraphs was successful:"+s)
        for(uri<-registeredGraphs) console.log(uri)
        ()
      })
      ()}
    )

  }

  def test(store: Store) {
    console.log(s"in rdfstore.create, was successful")
    console.log(store)
  }
}
