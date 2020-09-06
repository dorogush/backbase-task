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
package com.adorogush.backbasetask.controller;

import static org.springframework.http.ResponseEntity.created;

import com.adorogush.backbasetask.model.Game;
import com.adorogush.backbasetask.model.GameAndPits;
import com.adorogush.backbasetask.model.GameRestResponse;
import com.adorogush.backbasetask.service.GameService;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** REST controller for {@code /games} endpoints. */
@RestController
@RequestMapping(path = "/games")
public class GamesController {

  private final GameService gameService;

  public GamesController(final GameService gameService) {
    this.gameService = gameService;
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GameRestResponse> post(@Autowired final HttpServletRequest request) {
    final GameAndPits gameAndPits = gameService.createGame();
    final Game game = gameAndPits.game();
    final GameRestResponse gameRestResponse =
        new GameRestResponse(
            game.id(),
            buildUriWithGameId(request, game.id()),
            convertPitsToStatus(gameAndPits.pits()),
            game.nextPlayer(),
            game.gameOver());
    return created(gameRestResponse.uri()).body(gameRestResponse);
  }

  @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GameRestResponse> getOne(
      @Autowired final HttpServletRequest request, @PathVariable("gameId") final String gameId) {
    final GameAndPits gameAndPits = gameService.readGame(gameId);
    final Game game = gameAndPits.game();
    final GameRestResponse gameRestResponse =
        new GameRestResponse(
            game.id(),
            buildUriWithGameId(request, gameId),
            convertPitsToStatus(gameAndPits.pits()),
            game.nextPlayer(),
            game.gameOver());
    return ResponseEntity.ok(gameRestResponse);
  }

  @DeleteMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public void deleteOne(@PathVariable("gameId") final String gameId) {
    gameService.deleteGame(gameId);
  }

  @PutMapping(value = "/{gameId}/pits/{pitId}")
  public ResponseEntity<GameRestResponse> putPit(
      @Autowired final HttpServletRequest request,
      @PathVariable("gameId") final String gameId,
      @PathVariable("pitId") final int pitId) {
    final GameAndPits gameAndPits = gameService.makeMove(gameId, pitId - 1);
    final Game game = gameAndPits.game();
    final GameRestResponse gameRestResponse =
        new GameRestResponse(
            game.id(),
            buildUriWithGameId(request, gameId),
            convertPitsToStatus(gameAndPits.pits()),
            game.nextPlayer(),
            game.gameOver());
    return ResponseEntity.ok(gameRestResponse);
  }

  private static URI buildUriWithGameId(final HttpServletRequest request, final String gameId) {
    final ServletUriComponentsBuilder builder =
        ServletUriComponentsBuilder.fromContextPath(request);
    if (gameId != null) {
      builder.pathSegment("games").pathSegment(gameId);
    }
    return builder.build().toUri();
  }

  private static Map<String, String> convertPitsToStatus(final List<Integer> pits) {
    final Map<String, String> map = new LinkedHashMap<>();
    for (int i = 0; i < pits.size(); i++) {
      map.put(String.valueOf(i + 1), String.valueOf(pits.get(i)));
    }
    return map;
  }
}
