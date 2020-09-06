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
package com.adorogush.backbasetask.model;

import static java.util.Objects.requireNonNull;

/** Immutable data class that represents Game. */
public class Game {

  private final String id;
  private final Player nextPlayer;
  private final boolean gameOver;

  public Game(final String id, final Player nextPlayer, final boolean gameOver) {
    this.id = requireNonNull(id);
    this.nextPlayer = nextPlayer;
    this.gameOver = gameOver;
  }

  public String id() {
    return id;
  }

  public Player nextPlayer() {
    return nextPlayer;
  }

  public boolean gameOver() {
    return gameOver;
  }
}
