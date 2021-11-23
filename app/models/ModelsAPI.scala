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

case class MovieSummaryAPI(
  tconst: String,
  titleType: String,
  primaryTitle: String,
  originalTitle: String,
  crews: Seq[CrewAPI],
  principals: Seq[PrincipalAPI]
)

case class MovieNameAPI(
  name: String,
  rating: Double,
  numVotes: Int
)

object PrincipalAPI{
  implicit val principalFormat = Json.format[PrincipalAPI]
}

object CrewAPI{
  implicit val crewFormat = Json.format[CrewAPI]
}

object MovieSummaryAPI{
  implicit val movieSummaryFormat = Json.format[MovieSummaryAPI]
}

object MovieNameAPI{
  implicit val movieNameFormat = Json.format[MovieNameAPI]
}