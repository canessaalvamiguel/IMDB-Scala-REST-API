package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class ApiRestControllerSpec() extends PlaySpec with GuiceOneAppPerTest with Injecting  {

  "ApiRest GET" should {

    "list summary movie" in {
      val filter = "Carmencita"
      val controller = inject[APIController]
      val api = controller.getMovieSummary(filter).apply(FakeRequest(GET, "/api/movie/"))

      status(api) mustBe OK
      contentType(api) mustBe Some("application/json")
      contentAsString(api) must include (filter)
      contentAsString(api) must include ("tconst")
      contentAsString(api) must include ("titleType")
      contentAsString(api) must include ("primaryTitle")
      contentAsString(api) must include ("originalTitle")
      contentAsString(api) must include ("crews")
      contentAsString(api) must include ("principals")
    }

    "get validation error while getting movie summary" in {
      val filter = "x"
      val controller = inject[APIController]
      val api = controller.getMovieSummary(filter).apply(FakeRequest(GET, "/api/movie/"))

      status(api) mustBe BAD_REQUEST
      contentType(api) mustBe Some("application/json")
      contentAsString(api) must include ("Filter must have more than 3  letter")
    }

    "list one item" in {
      val genre = "Comedy"
      val controller = inject[APIController]
      val api = controller.getTopRatedMoviesByGenre(genre).apply(FakeRequest(GET, "/api/movie/genre/"))

      status(api) mustBe OK
      contentType(api) mustBe Some("application/json")
      contentAsString(api) must include ("name")
      contentAsString(api) must include ("rating")
      contentAsString(api) must include ("numVotes")
    }

    "get validation error while getting top rated movies" in {
      val genre = "x"
      val controller = inject[APIController]
      val api = controller.getTopRatedMoviesByGenre(genre).apply(FakeRequest(GET, "/api/movie/genre/"))

      status(api) mustBe BAD_REQUEST
      contentType(api) mustBe Some("application/json")
      contentAsString(api) must include ("Genre must have more than 3  letter")
    }
  }
}
