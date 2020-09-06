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

import com.adorogush.backbasetask.model.Player;
import java.util.List;
import java.util.stream.IntStream;

/** Each instance of this class is designed to process exactly one move of Kalah game. */
public class Move {

  private final int pitsPerPlayer;
  private final List<Integer> pits;
  private final int pitId;
  private final int playerOneKalahId;
  private final int playerTwoKalahId;
  private final List<Integer> playerOnePits;
  private final List<Integer> playerTwoPits;
  private boolean extraMove;
  private boolean gameOver;

  public Move(final int pitsPerPlayer, final List<Integer> pits, final int pitId) {
    this.pitsPerPlayer = pitsPerPlayer;
    this.pits = pits;
    this.pitId = pitId;
    playerOneKalahId = getKalahIdForPlayer(Player.ONE);
    playerTwoKalahId = getKalahIdForPlayer(Player.TWO);
    playerOnePits = pits.subList(0, playerOneKalahId);
    playerTwoPits = pits.subList(playerOneKalahId + 1, playerTwoKalahId);
  }

  public void move() {
    final Player initialPlayer = getPitOwnerPlayer(pitId);
    int stones = pits.get(pitId);
    if (stones <= 0) {
      return;
    }
    pits.set(pitId, 0);
    int currentPitId = pitId;
    boolean isKalah = false;
    boolean isMyPit = true;
    while (stones > 0) {
      currentPitId = nextPit(currentPitId);
      isKalah = isKalah(currentPitId);
      isMyPit = getPitOwnerPlayer(currentPitId) == initialPlayer;
      if (!isKalah || isMyPit) {
        add(currentPitId, 1);
        stones--;
      }
    }
    extraMove = isKalah;
    if (!isKalah && isMyPit && pits.get(currentPitId) == 1) {
      final int kalahId = getKalahIdForPlayer(initialPlayer);
      final int oppositePitId = oppositePitId(currentPitId);
      moveStones(currentPitId, kalahId);
      moveStones(oppositePitId, kalahId);
    }
    checkIfGameOver();
  }

  private Player getPitOwnerPlayer(final int pitId) {
    if (pitId < pitsPerPlayer + 1) {
      return Player.ONE;
    }
    return Player.TWO;
  }

  private int nextPit(final int pitId) {
    return (pitId + 1) % (pitsPerPlayer * 2 + 2);
  }

  private boolean isKalah(final int pitId) {
    return (pitId + 1) % (pitsPerPlayer + 1) == 0;
  }

  private void add(final int index, final int add) {
    pits.set(index, pits.get(index) + add);
  }

  private void moveStones(final int fromPitId, final int toPitId) {
    add(toPitId, pits.get(fromPitId));
    pits.set(fromPitId, 0);
  }

  private int oppositePitId(final int pitId) {
    return pitsPerPlayer * 2 - pitId;
  }

  private int getKalahIdForPlayer(final Player player) {
    if (player == Player.ONE) {
      return pitsPerPlayer;
    }
    return pitsPerPlayer * 2 + 1;
  }

  private void checkIfGameOver() {
    final int playerOneStonesLeft = playerOnePits.stream().mapToInt(v -> v).sum();
    final int playerTwoStonesLeft = playerTwoPits.stream().mapToInt(v -> v).sum();

    gameOver = playerOneStonesLeft == 0 || playerTwoStonesLeft == 0;
    if (gameOver) {
      IntStream.range(0, playerOnePits.size()).forEach(i -> playerOnePits.set(i, 0));
      IntStream.range(0, playerTwoPits.size()).forEach(i -> playerTwoPits.set(i, 0));

      add(playerOneKalahId, playerOneStonesLeft);
      add(playerTwoKalahId, playerTwoStonesLeft);
    }
  }

  public boolean isExtraMove() {
    return extraMove;
  }

  public boolean isGameOver() {
    return gameOver;
  }
}
