package bp.trainapp.route

import scala.util.{ Success, Failure }
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import spray.routing._
import spray.http._
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.httpx.SprayJsonSupport
import spray.json._

import reactivemongo.bson._

import bp.trainapp.controller.AuthController
import bp.trainapp.repository.RepositoryComponent
import bp.trainapp.utils.SprayAuthDirective
import bp.trainapp.model._
import bp.trainapp.service._

trait AuthRoute extends HttpService with AuthController {

  val authRoute =
    pathPrefix("islogin") {
      get {
        isLoginAction
      }
    } ~
    pathPrefix("login") {
      post {
        loginAction
      }
    } ~
    pathPrefix("fblogin") {
      post {
        fbLoginAction
      }
    } ~
    pathPrefix("logout") {
      post {
        logoutAction
      }
    }
}