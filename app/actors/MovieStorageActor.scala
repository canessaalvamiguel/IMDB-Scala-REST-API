package actors

import actors.MovieStorageActor.MovieSummary
import akka.pattern.pipe
import dao.MovieStorage
import lunatech.LunatechActor

object MovieStorageActor{
  sealed trait StorageMessage

  case class MovieSummary(filter: String)
      extends StorageMessage
}

class MovieStorageActor(moviesStorage: MovieStorage) extends LunatechActor{
  import context.dispatcher

  override def handleMessage: Receive = {
    case MovieSummary(filter) => moviesStorage.getMovieSummary(filter) pipeTo sender
  }
}
