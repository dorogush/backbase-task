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

import java.util.List;

/** Data class that holds a pair of Game and the current state of pits. */
public class GameAndPits {

  private final Game game;
  private final List<Integer> pits;

  public GameAndPits(final Game game, final List<Integer> pits) {
    this.game = requireNonNull(game);
    this.pits = requireNonNull(pits);
  }

  public Game game() {
    return game;
  }

  public List<Integer> pits() {
    return pits;
  }
}
