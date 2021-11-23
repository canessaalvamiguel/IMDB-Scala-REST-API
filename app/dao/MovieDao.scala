package dao

import com.google.inject.Inject
import models.{CrewAPI, MovieNameAPI, MovieSummaryAPI, Name, PrincipalAPI, TitleBasics, TitleCrew, TitlePrincipal, TitleRating}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.StructNode
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class MovieTableDef(tag: Tag) extends Table[TitleBasics](tag, "title_basics"){
  def tconst = column[String]("tconst")
  def titleBasics = column[Option[String]]("titletype")
  def primaryTitle = column[Option[String]]("primarytitle")
  def originalTitle = column[Option[String]]("originaltitle")
  def isAdult = column[Option[Boolean]]("isadult")
  def startYear = column[Option[Int]]("startyear")
  def endYear = column[Option[Int]]("endyear")
  def runtimeMinutes = column[Option[Int]]("runtimeminutes")
  def genres = column[Option[String]]("genres")
  override def * = (tconst, titleBasics, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres) <> (TitleBasics.tupled, TitleBasics.unapply)
}

class CrewTableDef(tag: Tag) extends Table[TitleCrew](tag, "title_crew"){
  def tconst = column[String]("tconst")
  def directors = column[Option[String]]("directors")
  def writers = column[Option[String]]("writers")
  override def * = (tconst, directors, writers) <> (TitleCrew.tupled, TitleCrew.unapply)
}

class PrincipalTableDef(tag: Tag) extends Table[TitlePrincipal](tag, "title_principals"){
  def tconst = column[String]("tconst")
  def ordering = column[Int]("ordering")
  def nconst = column[String]("nconst")
  def category = column[Option[String]]("category")
  def job = column[Option[String]]("job")
  def characters = column[Option[String]]("characters")
  override def * = (tconst, ordering, nconst, category, job, characters) <> (TitlePrincipal.tupled, TitlePrincipal.unapply)
}

class NameTableDef(tag: Tag) extends Table[Name](tag, "name_basics"){
  def nconst = column[String]("nconst")
  def primaryName = column[Option[String]]("primaryname")
  def birthYear = column[Option[Int]]("birthyear")
  def deathYear = column[Option[Int]]("deathyear")
  def primaryProfession = column[Option[String]]("primaryprofession")
  def knownForTitles = column[Option[String]]("knownfortitles")
  override def * = (nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles) <> (Name.tupled, Name.unapply)
}

class RatingTableDef(tag: Tag) extends Table[TitleRating](tag, "title_ratings"){
  def tconst = column[String]("tconst")
  def averageRating = column[Option[Double]]("averagerating")
  def numVotes = column[Option[Int]]("numvotes")
  override def * = (tconst, averageRating, numVotes) <> (TitleRating.tupled, TitleRating.unapply)
}

@Singleton
class MovieDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

  val movieList = TableQuery[MovieTableDef]
  val crewList = TableQuery[CrewTableDef]
  val principalList = TableQuery[PrincipalTableDef]
  val namelList = TableQuery[NameTableDef]
  val ratingList = TableQuery[RatingTableDef]

  def getMovieSummaryQuery(filter: String) = {
    val movieQuery = movieList.filter{
      movie =>
        movie.primaryTitle === filter || movie.originalTitle === filter
    }

    val withPrincipalQuery = for{
      (movie, principal) <- movieQuery.join(principalList).on(_.tconst === _.tconst)
    }yield (movie, principal)

    val withCrewQuery = for{
      (movie, crew) <- movieQuery.join(crewList).on(_.tconst === _.tconst)
    }yield (movie, crew)

    for{
      ((movie,_),namesPrincipal) <- withPrincipalQuery
        .join(namelList).on(_._2.nconst === _.nconst)
      ((_, _),namesWriters) <- withCrewQuery
        .joinLeft(namelList).on(_._2.writers === _.nconst)
      ((_, _),namesDirector) <- withCrewQuery
        .joinLeft(namelList).on(_._2.directors === _.nconst)
    }yield (movie,namesPrincipal,namesWriters,namesDirector)

  }

  def getMovieSummary(filter: String): Future[Try[Seq[MovieSummaryAPI]]] = {
    dbConfig.db.run(getMovieSummaryQuery(filter).result).map{ dataTuples  =>
      val grouperByMovie = dataTuples.groupBy(_._1)
      Success(grouperByMovie.map {
        case (movie,tuples) =>
          val principals = tuples.map(_._2).distinct.map{ p => PrincipalAPI(p.nconst, p.primaryName.getOrElse(""), p.birthYear.getOrElse(0))}
          val writers = tuples.flatMap(_._3).distinct.map{ w => CrewAPI(w.nconst, w.primaryName.getOrElse(""), w.birthYear.getOrElse(0), "writer")}
          val directors = tuples.flatMap(_._4).distinct.map{ d => CrewAPI(d.nconst, d.primaryName.getOrElse(""), d.birthYear.getOrElse(0), "director")}
          MovieSummaryAPI(
            movie.tconst,
            movie.titleType.getOrElse(""),
            movie.primaryTitle.getOrElse(""),
            movie.originalTitle.getOrElse(""),
            writers ++ directors,
            principals
          )
      }.toSeq)
    }.recover {
      case ex: Exception => {
        Failure(ex)
      }
    }
  }

  def getTopRatedMoviesByGenreQuery(genre: String) = {
    for{
      (movie, rating) <- movieList.join(ratingList)
        .on(_.tconst === _.tconst)
        .filter(_._1.genres  like s"%$genre%")
        .sortBy(s => (s._2.averageRating.desc, s._2.numVotes.desc, s._1.originalTitle.asc))
        .take(10)
    }yield (movie, rating)

  }

  def getTopRatedMoviesByGenre(genre: String): Future[Try[Seq[MovieNameAPI]]] = {
    dbConfig.db.run(getTopRatedMoviesByGenreQuery(genre).result).map{ dataTuples =>
      Success(dataTuples.map{
        case (movie, tuple) =>
          val rating = tuple.averageRating.getOrElse(0.0)
          val numVotes = tuple.numVotes.getOrElse(0)
          MovieNameAPI(movie.originalTitle.getOrElse(""),rating, numVotes)
      })
    }.recover {
      case ex: Exception => {
        Failure(ex)
      }
    }
  }
}
