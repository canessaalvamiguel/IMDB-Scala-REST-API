package dao

import com.google.inject.Inject
import models.MovieAPI

import javax.inject.Singleton
import scala.concurrent.Future
import scala.util.Try

@Singleton
class MovieStorage @Inject()(dao: MovieDao){

  def getMovieSummary(filter: String): Future[Try[Seq[MovieAPI]]] = dao.getMovieSummary(filter)
}
