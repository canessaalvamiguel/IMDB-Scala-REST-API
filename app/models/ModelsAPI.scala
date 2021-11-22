package models

import play.api.libs.json.Json

case class PrincipalAPI(
  nconst: String,
  primaryName: String,
  birthYear: Int
)

case class CrewAPI(
  nconst: String,
  primaryName: String,
  birthYear: Int,
  role : String
)

case class MovieAPI(
  tconst: String,
  titleType: String,
  primaryTitle: String,
  originalTitle: String,
  crews: Seq[CrewAPI],
  principals: Seq[PrincipalAPI]
)

object PrincipalAPI{
  implicit val principalFormat = Json.format[PrincipalAPI]
}

object CrewAPI{
  implicit val crewFormat = Json.format[CrewAPI]
}

object MoviAPI{
  implicit val movieFormat = Json.format[MovieAPI]
}