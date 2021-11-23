package controllers

import models._

import javax.inject._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import services.MovieService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class APIController @Inject()(cc : ControllerComponents, movieService: MovieService) extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())
  implicit val todoFormat = Json.format[MovieAPI]

  def getMovieSummary(filter: String) = Action.async { implicit request: Request[AnyContent] =>

    if(filter.isEmpty || filter.length <= 3) {
     Future.successful( BadRequest(Json.obj("error" -> "Filter must have more than 3  letter")))
    }else{
      movieService.getMovieSummary(filter) map {
        case Success(movie) => Ok(Json.toJson(movie))
        case Failure(exception) =>
          logger.error(s"Error while reading data, exception: ${exception}")
          BadRequest(Json.obj("error" -> "Something went wrong. Please try again later."))
      }
    }
  }
}
