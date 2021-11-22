package models

case class TitleBasics(tconst: String, titleType: Option[String], primaryTitle: Option[String], originalTitle: Option[String], isAdult: Option[Boolean], startYear: Option[Int], endYear: Option[Int], runtimeMinutes: Option[Int], genres: Option[String])
case class TitleRating(tconst: String, averageRating: String, numVotes: String)
case class TitleCrew(tconst: String, directors: Option[String], writers: Option[String])
case class TitlePrincipal(tconst: String, ordering: Int, nconst: String, category: Option[String], job: Option[String], characters: Option[String])
case class Name(nconst: String, primaryName: Option[String], birthYear: Option[Int], deathYear: Option[Int], primaryProfession: Option[String], knownForTitles: Option[String])