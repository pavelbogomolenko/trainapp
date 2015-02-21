package bp.trainapp.controller

import spray.routing._
import spray.http._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.httpx.SprayJsonSupport
import spray.json._

import bp.trainapp.utils.SprayAuthDirective

trait BaseController extends HttpService with SprayJsonSupport with SprayAuthDirective {
  type Response = Route
}