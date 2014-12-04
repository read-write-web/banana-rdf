package org.w3.banana.rdfstore

import scala.scalajs.js
import org.w3.banana.rdfstorew.{RDFStoreGraph, JSStore}
import scala.concurrent.Promise
import scala.scalajs.js.annotation.JSName


trait Store extends js.Object {

  type CallBack[A] = js.Function2[Boolean,js.Any,Promise[A]]

  def execute(sparql: String, cb: CallBack[Any]): Unit = ???

  @JSName("execute")
  def executeUnit(sparql: String, cb: CallBack[Unit]): Unit = ???

  def delete(node: js.Dynamic, cb: CallBack[Unit]): Unit = ???

  def delete(node: js.Dynamic, triples: js.String, cb: CallBack[Unit]): Unit = ???

  def insert(node: js.Dynamic, cb: CallBack[Unit])

  def insert(node: js.Dynamic, triples: js.String, cb: CallBack[Unit]): Unit = ???

  def clear(cb: CallBack[Unit]): Unit = ???

  def clear(uri: js.String, cb: CallBack[Unit]): Unit = ???

  def graph(uri: js.String, cb: CallBack[JSStore#Graph]): Unit = ???

  def load(mediaType: String,data: String, cb: CallBack[JSStore#Graph]): Unit = ???

  def load(mediaType: String,data: String, uri: String, cb: CallBack[JSStore#Graph]): Unit = ???

  def rdf: js.Dynamic = ???

  //todo
  //def create()

}


