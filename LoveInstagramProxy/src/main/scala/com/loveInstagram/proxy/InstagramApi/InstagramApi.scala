package com.loveInstagramApi.api

import com.loveInstagramApi.rest._

import spray.client.pipelining._
import spray.http.FormData
import spray.http._
import spray.routing._

import akka.actor.Actor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await} 
import scala.concurrent.duration._

import java.util.concurrent.TimeUnit

import scala.util.matching.Regex

trait InstagramApi extends Actor {
  implicit def actorRefFactory = context
  private val pipeline = sendReceive
  private val instagramAccessTokenUri = "https://api.instagram.com/oauth/access_token"

  private def instagramTagsUri(tag: String, accessToken: String) = s"https://api.instagram.com/v1/tags/$tag/media/recent?access_token=$accessToken"
  private def likeMedia(mediaId: String) = s"https://api.instagram.com/v1/media/$mediaId/likes"

  def await[A](f: Future[A]): A = {
    val futureTimeout = Duration(2, TimeUnit.MINUTES)
    Await.result(f, futureTimeout)
  }

  def getAccessToken(clientId: String, clientSecret: String, code: String): String = {

    val response = pipeline(Post(instagramAccessTokenUri, FormData(Map(
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "grant_type" -> "authorization_code",
      "redirect_uri" -> "http://localhost:3000/api",
      "code" -> code
    ))))

    await(response).entity.asString.dropWhile(_!=':').tail.takeWhile(_!=',')    
  }

  def likeRecentTagedMedia(accessToken: String, tags: List[String]): String = {
    val idPattern = """(?<=false,"id":")(\d*\w\d*)(?=",)""".r  //This will filter only ones we haven't liked yet
    val photosToLikePerTag = 2

    val idResponses = tags.map { tag =>
      val html = await(pipeline(Get(instagramTagsUri(tag, accessToken)))).entity.asString
      idPattern.findAllIn(html).toList.take(photosToLikePerTag)
    }.flatten

    val likeResponses = idResponses.foldRight(List[String]()){(id, acc) =>
      val html = await(pipeline(Post(likeMedia(id), FormData(Map(
        "access_token" -> accessToken
      ))))).entity.asString

      if(html.contains(""""code":200""")){
        id :: acc
      }else{
        acc 
      }
    }
    
    likeResponses.length.toString
  }

}