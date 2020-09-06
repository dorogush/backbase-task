/*
* Copyright 2020 Aleksandr Dorogush
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.adorogush.backbasetask.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.adorogush.backbasetask.model.GameRestResponse;
import com.adorogush.backbasetask.model.Player;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/** The main integration tests class. Starts the whole application and sends real REST requests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class KalahTest {

  private final TestRestTemplate testRestTemplate;

  public KalahTest(
      @LocalServerPort final int localPort,
      @Autowired final RestTemplateBuilder restTemplateBuilder) {
    testRestTemplate =
        new TestRestTemplate(
            restTemplateBuilder
                .rootUri("http://localhost:" + localPort)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
  }

  @Test
  void basicScenario() {
    final GameRestResponse game = createGame();
    move(game.id(), 1, 0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0);
    move(game.id(), 3, 0, 7, 0, 8, 8, 8, 2, 7, 7, 7, 6, 6, 6, 0);
    move(game.id(), 8, 1, 7, 0, 8, 8, 8, 2, 0, 8, 8, 7, 7, 7, 1);
    move(game.id(), 4, 1, 7, 0, 0, 9, 9, 3, 1, 9, 9, 8, 8, 7, 1);
    move(game.id(), 9, 2, 8, 1, 1, 9, 9, 3, 1, 0, 10, 9, 9, 8, 2);
    move(game.id(), 3, 2, 8, 0, 2, 9, 9, 3, 1, 0, 10, 9, 9, 8, 2);
    move(game.id(), 8, 2, 8, 0, 2, 0, 9, 3, 0, 0, 10, 9, 9, 8, 12);
    move(game.id(), 1, 0, 9, 0, 2, 0, 9, 13, 0, 0, 10, 0, 9, 8, 12);
    move(game.id(), 12, 1, 10, 1, 3, 1, 0, 13, 0, 0, 10, 0, 0, 9, 24);
    move(game.id(), 4, 1, 10, 1, 0, 2, 1, 14, 0, 0, 10, 0, 0, 9, 24);
    move(game.id(), 3, 1, 10, 0, 0, 2, 1, 25, 0, 0, 0, 0, 0, 9, 24);
    move(game.id(), 13, 2, 11, 1, 1, 0, 2, 25, 1, 0, 0, 0, 0, 0, 29);
    move(game.id(), 4, 2, 11, 1, 0, 0, 2, 26, 1, 0, 0, 0, 0, 0, 29);
    move(game.id(), 8, 0, 0, 0, 0, 0, 0, 42, 0, 0, 0, 0, 0, 0, 30);
    final GameRestResponse gameFound = readGame(game.id());
    assertThat(gameFound.gameOver(), equalTo(true));

    // assert attempt to move returns game over
    assertMoveReturnsError(game.id(), 1, "Game is over.");
  }

  @Test
  void testGameCreateReturnsValidResponse() {
    // when
    final GameRestResponse game = createGame();
    // then
    assertThat(game.id(), notNullValue());
    assertThat(game.uri().toString(), endsWith("/games/" + game.id()));
    assertThat(game.gameOver(), equalTo(false));
    assertThat(game.nextPlayer(), nullValue());
    assertThat(game.status(), equalTo(pits(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0)));
  }

  @Test
  void testGameMoveReturnsValidResponse() {
    // given
    final GameRestResponse game = createGame();
    // when
    final GameRestResponse gameStatus = move(game.id(), 1);
    assertThat(gameStatus.id(), equalTo(game.id()));
    assertThat(gameStatus.uri(), equalTo(game.uri()));
    assertThat(gameStatus.gameOver(), equalTo(false));
    assertThat(gameStatus.nextPlayer(), equalTo(Player.ONE));
    assertThat(gameStatus.status(), equalTo(pits(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0)));
  }

  @Test
  void testGameReadReturnsValidResponse() {
    // given
    final GameRestResponse game = createGame();
    move(game.id(), 1);
    // when
    final GameRestResponse gameFound = readGame(game.id());
    assertThat(gameFound.id(), equalTo(game.id()));
    assertThat(gameFound.uri(), equalTo(game.uri()));
    assertThat(gameFound.gameOver(), equalTo(false));
    assertThat(gameFound.nextPlayer(), equalTo(Player.ONE));
    assertThat(gameFound.status(), equalTo(pits(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0)));
  }

  @Test
  void testMoveEmptyPitDoesNothing() {
    // given
    final GameRestResponse game = createGame();
    move(game.id(), 1);
    // when
    final GameRestResponse gameState = move(game.id(), 1);
    // then
    assertThat(gameState.status(), equalTo(pits(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0)));
  }

  @Test
  void testMoveWrongPlayersPitReturnsBadReqest() {
    // given
    final GameRestResponse game = createGame();
    move(game.id(), 2);
    // when / then
    assertMoveReturnsError(game.id(), 3, "Wrong player's move.");
  }

  @Test
  void testMoveOutOfRangePitReturnsBadReqest() {
    // given
    final GameRestResponse game = createGame();
    // when / then
    assertMoveReturnsError(game.id(), 15, "Wrong pit id.");
    assertMoveReturnsError(game.id(), 0, "Wrong pit id.");
  }

  @Test
  void testMoveKalahReturnsBadReqest() {
    // given
    final GameRestResponse game = createGame();
    // when / then
    assertMoveReturnsError(game.id(), 14, "Pit is Kalah.");
  }

  @Test
  void testGameDeletedReturnsNotFound() {
    // given
    final GameRestResponse game = createGame();
    // when
    deleteGame(game.id());
    // then
    final ResponseEntity<String> response =
        testRestTemplate.getForEntity("/games/{gameId}", String.class, game.id());
    assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), equalTo("Could not find Game " + game.id()));
  }

  @Test
  void testGameDeleteNotFound() {
    // when
    final ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/games/{gameId}", HttpMethod.DELETE, null, String.class, "invalid");
    // then
    assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), equalTo("Could not find Game invalid"));
  }

  @Test
  void testReadGameReturnsNotFound() {
    // when
    final ResponseEntity<String> response =
        testRestTemplate.getForEntity("/games/{gameId}", String.class, "invalid");
    // then
    assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), equalTo("Could not find Game invalid"));
  }

  @Test
  void testInvalidEndpointReturns404() {
    // when
    final ResponseEntity<String> response = testRestTemplate.getForEntity("/invalid", String.class);
    // then
    assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
  }

  @Test
  void testInvalidMethodReturns405() {
    // when
    final ResponseEntity<String> response =
        testRestTemplate.exchange("/games", HttpMethod.PUT, null, String.class);
    // then
    assertThat(response.getStatusCode(), equalTo(HttpStatus.METHOD_NOT_ALLOWED));
  }

  private GameRestResponse createGame() {
    final ResponseEntity<GameRestResponse> response =
        testRestTemplate.postForEntity("/games", null, GameRestResponse.class);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    return response.getBody();
  }

  private GameRestResponse readGame(final String gameId) {
    final ResponseEntity<GameRestResponse> response =
        testRestTemplate.getForEntity("/games/{gameId}", GameRestResponse.class, gameId);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    return response.getBody();
  }

  private void deleteGame(final String gameId) {
    final ResponseEntity<Void> response =
        testRestTemplate.exchange("/games/{gameId}", HttpMethod.DELETE, null, Void.class, gameId);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
  }

  private void move(final String gameId, final int pitToMove, final int... pitsExpectedAfterMove) {
    final GameRestResponse state = move(gameId, pitToMove);
    assertThat(state.status(), equalTo(pits(pitsExpectedAfterMove)));
  }

  private GameRestResponse move(final String gameId, final int pitId) {
    final ResponseEntity<GameRestResponse> response =
        testRestTemplate.exchange(
            "/games/{gameId}/pits/{pitId}",
            HttpMethod.PUT,
            null,
            GameRestResponse.class,
            gameId,
            pitId);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    return response.getBody();
  }

  private static Map<String, String> pits(final int... pit) {
    final Map<String, String> map = new LinkedHashMap<>();
    for (int i = 0; i < pit.length; i++) {
      map.put(String.valueOf(i + 1), String.valueOf(pit[i]));
    }
    return map;
  }

  private void assertMoveReturnsError(final String gameId, final int pitId, final String message) {
    final ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/games/{gameId}/pits/{pitId}", HttpMethod.PUT, null, String.class, gameId, pitId);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody(), equalTo(message));
  }
}
