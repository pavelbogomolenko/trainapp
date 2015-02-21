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

import bp.trainapp.controller.TrainingController
import bp.trainapp.repository.RepositoryComponent
import bp.trainapp.model._
import bp.trainapp.service._

trait TrainingRoute extends HttpService with TrainingController {

  val trainingRoute =
    pathPrefix("training") {
      get {
        getLastTrainingAction
      } ~
      post {
        createTrainingAction
      } ~
      put {
        updateTrainingAction
      }
    }
}