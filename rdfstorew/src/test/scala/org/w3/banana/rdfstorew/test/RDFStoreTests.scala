package org.w3.banana.rdfstorew.test


import org.w3.banana.jasmine.test._
import org.w3.banana.rdfstorew._


object PointedGraphJasmineTesterRDFStore extends PointedGraphJasmineTester[JSStore]

//object GraphUnionJasmineTest extends GraphUnionJasmineTest[RDFStore]

object DieselGraphConstructJasmineTest extends DieselGraphConstructJasmineTest[JSStore]

object RDFStoreWDieselGraphExplorationJasmineTest extends DieselGraphExplorationJasmineTest[JSStore]

object CommonBindersJasmineTest extends CommonBindersJasmineTest[JSStore]

object RecordBinderJasmineTest extends RecordBinderJasmineTest[JSStore]

object UriSyntaxJasmineTest extends UriSyntaxJasmineTest[JSStore]

//object TurtleTestJasmineSuite extends TurtleTestJasmineSuite[RDFStore]

//object GraphStoreJasmineTest extends GraphStoreJasmineTest[RDFStore,scalajs.js.Dynamic](RDFStoreW.makeRDFStoreJS(Map()))

object SparqlEngineJasmineTest extends SparqlEngineJasmineTest[JSStore,scalajs.js.Dynamic](RDFStoreW.makeRDFStoreJS(Map()))

//object StandardIsomorphismTest extends IsomorphismTests[RDFStore]

//TODO:
// - Extract the store JS code to an external library. Webjars maybe?