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
package com.adorogush.backbasetask.service;

import com.adorogush.backbasetask.exception.NotFoundException;
import com.adorogush.backbasetask.exception.ValidationException;
import com.adorogush.backbasetask.model.Game;
import com.adorogush.backbasetask.model.GameAndPits;
import com.adorogush.backbasetask.model.Player;
import com.adorogush.backbasetask.repository.GameAndPitRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** A Service for CRUD Game operations. */
@Service
@Transactional
public class GameService {

  private final int numberOfPits;
  private final int numberOfStones;
  private final GameAndPitRepository gameAndPitRepository;

  public GameService(
      @Value("${numberOfPits}") final int numberOfPits,
      @Value("${numberOfStones}") final int numberOfStones,
      final GameAndPitRepository gameAndPitRepository) {
    this.numberOfPits = numberOfPits;
    this.numberOfStones = numberOfStones;
    this.gameAndPitRepository = gameAndPitRepository;
  }

  public GameAndPits createGame() {
    final Game game = gameAndPitRepository.createEmptyGame();
    final List<Integer> pits =
        gameAndPitRepository.createInitialPits(game.id(), numberOfPits, numberOfStones);
    return new GameAndPits(game, pits);
  }

  public GameAndPits readGame(final String gameId) {
    final Game game = gameAndPitRepository.readGame(gameId).orElseThrow(() -> gameNotFound(gameId));
    final List<Integer> pits = gameAndPitRepository.readPits(gameId);
    return new GameAndPits(game, pits);
  }

  public void deleteGame(final String gameId) {
    final boolean deleted = gameAndPitRepository.deleteGame(gameId);
    if (!deleted) {
      throw gameNotFound(gameId);
    }
  }

  private static void validatePlayer(final Player nextPlayer, final Player initialPlayer) {
    if (nextPlayer != null && nextPlayer != initialPlayer) {
      throw new ValidationException("Wrong player's move.");
    }
  }

  private static void validateGameOver(final boolean gameOver) {
    if (gameOver) {
      throw new ValidationException("Game is over.");
    }
  }

  public GameAndPits makeMove(final String gameId, final int pitId) {
    validatePitIdRange(pitId);
    validateIsNotKalah(pitId);
    final GameAndPits gameAndPits = readGame(gameId);
    final Game game = gameAndPits.game();
    validateGameOver(game.gameOver());
    final Player player = getPitOwnerPlayer(pitId);
    validatePlayer(game.nextPlayer(), player);
    final List<Integer> pits = new ArrayList<>(gameAndPits.pits());
    final Move move = new Move(numberOfPits, pits, pitId);
    move.move();
    gameAndPitRepository.updatePits(gameId, pits);
    final Player nextPlayer = move.isExtraMove() ? player : player.opponent();
    final boolean gameOver = move.isGameOver();
    gameAndPitRepository.updateGame(gameId, nextPlayer, gameOver);
    return new GameAndPits(new Game(gameId, nextPlayer, gameOver), pits);
  }

  private static NotFoundException gameNotFound(final String id) {
    return new NotFoundException(String.format("Could not find Game %s", id));
  }

  public Player getPitOwnerPlayer(final int pitId) {
    if (pitId < numberOfPits + 1) {
      return Player.ONE;
    }
    return Player.TWO;
  }

  public void validatePitIdRange(final int pitId) {
    if (pitId < 0 || pitId >= numberOfPits * 2 + 2) {
      throw new ValidationException("Wrong pit id.");
    }
  }

  public void validateIsNotKalah(final int pitId) {
    if ((pitId + 1) % (numberOfPits + 1) == 0) {
      throw new ValidationException("Pit is Kalah.");
    }
  }
}
