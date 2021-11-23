package services

import actors.MovieStorageActor
import actors.MovieStorageActor.{MovieNamesBySummary, MovieSummary}
import akka.actor.{ActorSystem, Props}

import javax.inject.Singleton
import scala.concurrent.Future
import scala.concurrent.duration.MINUTES
import scala.util.Try
import akka.pattern.ask
import com.google.inject.Inject
import dao.MovieStorage
import models.{MovieNameAPI, MovieSummaryAPI}

@Singleton
class MovieService @Inject() (moviesStorage: MovieStorage){

  implicit val timeout = akka.util.Timeout(5, MINUTES)

  object MovieStorage{
    def props: Props = Props(new MovieStorageActor(moviesStorage))
  }

  val system = ActorSystem("MovieActorSystem")
  val movieStorage = system.actorOf(MovieStorage.props, "MovieActorStorage")

  def getMovieSummary(filter: String) : Future[Try[Seq[MovieSummaryAPI]]] = {
      (movieStorage ?  MovieSummary(filter)).mapTo[Try[Seq[MovieSummaryAPI]]]
  }

  def getTopRatedMoviesByGenre(genre: String): Future[Try[Seq[MovieNameAPI]]] = {
    (movieStorage ?  MovieNamesBySummary(genre)).mapTo[Try[Seq[MovieNameAPI]]]
  }

}
