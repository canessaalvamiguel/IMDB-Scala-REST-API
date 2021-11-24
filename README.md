# IMDB Rest API

IMDB Rest API is a webservice based on Scala and Play Framework that helps users get valuable information regarding IMDB movies.

## Endpoints

We have two endpoints avaible.

| Endpoint | Use |
| ------ | ------ |
| GET ```/api/movie/:filter``` | Present the user with endpoint for allowing them to search by movie’s primary title or original title. The outcome includes information to that title, including cast and crew. Filter length must be greather than 3.|
| GET ```/api/movie/genre/:genre``` | Present the user with endpoint for allowing them to search the top rated movies for a genre. Genre length must be greather than 3.|

## Responses

```For the first endpoint you will get the next JSON structure```
```GET http://localhost:9000/api/movie/Carmencita```
```
[
    {
        "tconst": "string",
        "titleType": "string",
        "primaryTitle": "string",
        "originalTitle": "string",
        "crews": [
            {
                "nconst": "string",
                "primaryName": "string",
                "birthYear": "integer",
                "role": "string"
            }
        ],
        "principals": [
            {
                "nconst":"string",
                "primaryName": "string",
                "birthYear": "integer"
            },
            {
                "nconst": "string",
                "primaryName": "string",
                "birthYear": "integer"
            },
            {
                "nconst": "string",
                "primaryName": "string",
                "birthYear": "integer"
            }
        ]
    }
]
```

```For the second endpoint you will get the next JSON structure```
```GET http://localhost:9000/api/movie/genre/Comedy```

```
[
    {
        "name": "string",
        "rating": "integer",
        "numVotes": "integer"
    }
]
```

## Run in localhost

You need to have sbt and scala installed

```sh
cd mdb_17112021_MiguelAngelAlva
sbt run
```
Then you may access using: ```http://localhost:9000/api/movie/genre/Comedy```

## Run tests

```sh
cd mdb_17112021_MiguelAngelAlva
sbt test
```