package bp.trainapp.utils

import spray.http.{ SomeOrigins, HttpMethods, HttpMethod, HttpResponse }
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.routing._

import bp.trainapp.service.AppConfig

/**
 * A mixin to provide support for CORS headers
 * Implementation inspired by this thread https://gist.github.com/joseraya/176821d856b43b1cfe19
 */
trait CorsSupport extends HttpService with AppConfig { 

  private val allowOriginHeader = `Access-Control-Allow-Origin`(
      SomeOrigins(
        Seq(CorsConfig.allowOrigin)
      )
  )
  private val optionsCorsHeaders = List(
    `Access-Control-Allow-Headers`(
      "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, " +
        "Referer, User-Agent, X-AUTH"),
    `Access-Control-Max-Age`(CorsConfig.controlMaxAge)
    )

  def cors[T]: Directive0 = mapRequestContext {
    context =>
      context.withRouteResponseHandling {
        // If an OPTIONS request was rejected as 405, complete the request by responding with the
        // defined CORS details and the allowed options grabbed from the rejection
        case Rejected(reasons) if (
          context.request.method == HttpMethods.OPTIONS &&
          reasons.exists(_.isInstanceOf[MethodRejection])) => {
          val allowedMethods = reasons.collect { case r: MethodRejection => r.supported }

          context.complete(HttpResponse().withHeaders(
            `Access-Control-Allow-Methods`(OPTIONS, allowedMethods: _*) ::
              allowOriginHeader ::
              `Access-Control-Allow-Credentials`(true) ::
              optionsCorsHeaders))
        }
      } withHttpResponseHeadersMapped { headers => allowOriginHeader :: `Access-Control-Allow-Credentials`(true) :: headers }
  }
}