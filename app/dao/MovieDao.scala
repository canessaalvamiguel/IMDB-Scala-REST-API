package dao

import com.google.inject.Inject
import models.{CrewAPI, MovieAPI, Name, PrincipalAPI, TitleBasics, TitleCrew, TitlePrincipal}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
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

@Singleton
class MovieDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

  var movieList = TableQuery[MovieTableDef]
  var crewList = TableQuery[CrewTableDef]
  var principalList = TableQuery[PrincipalTableDef]
  var namelList = TableQuery[NameTableDef]

  def getMovieQuery(filter: String) = ???

  def getMovieSummary(filter: String): Future[Try[Seq[MovieAPI]]] = ???
}
