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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.Map;

/** Data class that represents the REST response model. */
public class GameRestResponse {

  private final String id;
  private final URI uri;
  private final Map<String, String> status;
  private final Player nextPlayer;
  private final boolean gameOver;

  public GameRestResponse(
      @JsonProperty("id") final String id,
      @JsonProperty("uri") final URI uri,
      @JsonProperty("status") final Map<String, String> status,
      @JsonProperty("nextPlayer") final Player nextPlayer,
      @JsonProperty("gameOver") final boolean gameOver) {
    this.id = requireNonNull(id);
    this.uri = requireNonNull(uri);
    this.status = requireNonNull(status);
    this.nextPlayer = nextPlayer;
    this.gameOver = gameOver;
  }

  @JsonProperty("id")
  public String id() {
    return id;
  }

  @JsonProperty("uri")
  public URI uri() {
    return uri;
  }

  @JsonProperty("status")
  public Map<String, String> status() {
    return status;
  }

  @JsonProperty("nextPlayer")
  public Player nextPlayer() {
    return nextPlayer;
  }

  @JsonProperty("gameOver")
  public boolean gameOver() {
    return gameOver;
  }
}
