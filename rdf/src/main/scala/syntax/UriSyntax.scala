package org.w3.banana.syntax

import org.w3.banana._
import java.net.{ URI => jURI }

trait URISyntax[Rdf <: RDF] {
  this: RDFOperationsSyntax[Rdf] =>

  implicit def uriWrapper(uri: Rdf#URI): URIW = new URIW(uri)

  class URIW(uri: Rdf#URI) {

    def getString: String = ops.fromUri(uri)

    def fragmentLess: Rdf#URI = {
      val uriString = ops.fromUri(uri)
      val juri = new jURI(uriString)
      import juri._
      val uriNoFrag = new jURI(getScheme, getUserInfo, getHost, getPort, getPath, getQuery, null)
      ops.makeUri(uriNoFrag.toString)
    }

    def fragment(frag: String): Rdf#URI = {
      val uriString = ops.fromUri(uri)
      val juri = new jURI(uriString)
      import juri._
      val uriWithFrag = new jURI(getScheme, getUserInfo, getHost, getPort, getPath, getQuery, frag)
      ops.makeUri(uriWithFrag.toString)
    }

    def fragment: Option[String] = {
      val uriString = ops.fromUri(uri)
      val juri = new jURI(uriString)
      Option(juri.getFragment)
    }

    def newChildUri(): Rdf#URI = {
      val newId = java.util.UUID.randomUUID().toString
      val juri = new jURI(ops.fromUri(uri) + "/").resolve(newId)
      ops.makeUri(juri.toString)
    }

    def resolve(str: String): Rdf#URI = org.w3.banana.URI.resolve(uri, str)(ops)

    def resolveAgainst(other: Rdf#URI): Rdf#URI = org.w3.banana.URI.resolve(other, uri.toString)(ops)

    def relativize(other: Rdf#URI): Rdf#URI = org.w3.banana.URI.relativize(uri, other)(ops)

  }

}

