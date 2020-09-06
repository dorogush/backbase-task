# Backbase
This is a test task for a backend developer from [backbase.com](https://backbase.com).

## Task description
The goal of this task is to implement a REST api to play [Kalah](https://en.wikipedia.org/wiki/Kalah) game.
Complete task description can be found in [document](docs/Backbase_Coding_Challenge.pdf).

## Prerequisites
* OpenJDK 11

## Run
```
./mvnw spring-boot:run
```

## Game model
*Field* | *Type* | *Read only* | *Comment*
---|---|---|---
id | String | true | App will auto generate it.
uri | String | true | Absolute url to Game REST resource.
status | Integer | true | Map where each key is a string id of the Pit (1-based) and <br/>value is the current number of stones in this pit (also string).
nextPlayer | Integer | true | The id of player who's turn is next. `null` for new created game.
gameOver | Boolean | true | `true` if the game is over.

## REST endpoint

### Create
```
POST /games
```
Example
```
curl \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-X POST http://127.0.0.1:8080/games
{
  "id": "34490f2d-ddfc-44f0-9959-d5133d13b65a",
  "uri": "http://127.0.0.1:8080/games/34490f2d-ddfc-44f0-9959-d5133d13b65a",
  "status": {
    "1": "6",
    "2": "6",
    "3": "6",
    "4": "6",
    "5": "6",
    "6": "6",
    "7": "0",
    "8": "6",
    "9": "6",
    "10": "6",
    "11": "6",
    "12": "6",
    "13": "6",
    "14": "0"
  },
  "nextPlayer": null,
  "gameOver": false
}
```

### Read game
```
GET /games/{gameId}
```
Example
```
curl \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-X GET http://127.0.0.1:8080/games/34490f2d-ddfc-44f0-9959-d5133d13b65a
{
  "id": "34490f2d-ddfc-44f0-9959-d5133d13b65a",
  "uri": "http://127.0.0.1:8080/games/34490f2d-ddfc-44f0-9959-d5133d13b65a",
  "status": {
    "1": "6",
    "2": "6",
    "3": "6",
    "4": "6",
    "5": "6",
    "6": "6",
    "7": "0",
    "8": "6",
    "9": "6",
    "10": "6",
    "11": "6",
    "12": "6",
    "13": "6",
    "14": "0"
  },
  "nextPlayer": null,
  "gameOver": false
}
```

### Make a move
```
PUT /games/{gameId}/pits/{pitId}
```
Example
```
curl \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-X PUT http://127.0.0.1:8080/games/34490f2d-ddfc-44f0-9959-d5133d13b65a/pits/1
{
  "id": "34490f2d-ddfc-44f0-9959-d5133d13b65a",
  "uri": "http://127.0.0.1:8080/games/34490f2d-ddfc-44f0-9959-d5133d13b65a",
  "status": {
    "1": "0",
    "2": "7",
    "3": "7",
    "4": "7",
    "5": "7",
    "6": "7",
    "7": "1",
    "8": "6",
    "9": "6",
    "10": "6",
    "11": "6",
    "12": "6",
    "13": "6",
    "14": "0"
  },
  "nextPlayer": 0,
  "gameOver": false
}
```

### Delete game
```
DELETE /games/{gameId}
```
Example
```
curl \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-X DELETE http://127.0.0.1:8080/games/34490f2d-ddfc-44f0-9959-d5133d13b65a
```

## Spring profiles
There are 2 spring profiles `prod` (default) and `dev`.
The main difference is logging configuration:
* `prod` outputs in json format and all levels to `info`.
* `dev` outputs in plain text and `com.adorogush.backbasetask` level is `debug`.

## Configuration properties
*Key* | *Default value* | *Description*
---|---|---
numberOfPits | 6 | Number of pits per each player.
numberOfStones | 6 | Number of stones in each pit.

## Code style
This project follows [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
