package com.loveInstagramApi.rest

import com.loveInstagramApi.api._

import akka.event.slf4j.SLF4JLogging
import scala.Some
import spray.http._
import spray.client._
import spray.httpx.unmarshalling._
import spray.http.HttpHeaders.RawHeader
import spray.routing._

/**
 * REST Service actor.
 */
class RestServiceActor extends RestService {
  def receive = runRoute(rest)
}

/**
 * REST Service
 */
trait RestService extends HttpService with SLF4JLogging with InstagramApi {

  implicit val executionContext = actorRefFactory.dispatcher

  //Restructure this route to have api before "auth" and "likeMedia"
  val rest : Route = respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
    respondWithMediaType(MediaTypes.`application/json`) {   
      (path("api") & parameter('clientId, 'clientSecret, 'code) & get) { (clientId, clientSecret, code) =>
        complete {
          getAccessToken(clientId, clientSecret, code)
        }
      } ~
      (path("likeMedia") & parameter('accessToken, 'tags) & get) {(accessToken, tags) =>
        complete{
          val parsedTags = tags.split(',').toList
          likeRecentTagedMedia(accessToken, parsedTags)
        }
      } 
    }
  }
}