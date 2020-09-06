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
package com.adorogush.backbasetask.repository;

import com.adorogush.backbasetask.model.Game;
import com.adorogush.backbasetask.model.Player;
import com.adorogush.backbasetask.service.IdProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

/** H2 in-memory db SQL based repository implementation. */
@Repository
public class GameAndPitRepository {

  private final IdProvider idProvider;
  private final JdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert gameTableInsert;
  private final SimpleJdbcInsert pitTableInsert;

  public GameAndPitRepository(final IdProvider idProvider, final JdbcTemplate jdbcTemplate) {
    this.idProvider = idProvider;
    this.jdbcTemplate = jdbcTemplate;
    this.gameTableInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("game");
    this.pitTableInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("pit");
  }

  public Game createEmptyGame() {
    final Game game = new Game(idProvider.get(), null, false);
    gameTableInsert.execute(gameToJdbcMap(game));
    return game;
  }

  public List<Integer> createInitialPits(
      final String gameId, final int numberOfPits, final int numberOfStones) {
    final int capacity = numberOfPits * 2 + 2;
    final ArrayList<Integer> pits = new ArrayList<>(capacity);
    for (int i = 0; i < capacity; i++) {
      final boolean isKalah = (i + 1) % (numberOfPits + 1) == 0;
      pits.add(isKalah ? 0 : numberOfStones);
    }
    insertInitialPits(gameId, pits);
    return pits;
  }

  private void insertInitialPits(final String gameId, final List<Integer> pits) {
    @SuppressWarnings("unchecked")
    final Map<String, ?>[] batch = new Map[pits.size()];
    for (int i = 0; i < batch.length; i++) {
      batch[i] = pitToJdbcMap(gameId, i, pits.get(i));
    }
    pitTableInsert.executeBatch(batch);
  }

  public Optional<Game> readGame(final String gameId) {
    final List<Game> found =
        jdbcTemplate.query(
            "select * from game where id = ?", GameAndPitRepository::gameRowMapper, gameId);
    return Optional.of(found).filter(l -> !l.isEmpty()).map(l -> l.get(0));
  }

  public void updateGame(final String gameId, final Player nextPlayer, final boolean gameOver) {
    jdbcTemplate.update(
        "update game set nextPlayer = ?, gameOver = ? where id = ?",
        nextPlayer.index(),
        gameOver,
        gameId);
  }

  public boolean deleteGame(final String gameId) {
    final int updated = jdbcTemplate.update("delete from game where id = ?", gameId);
    return updated > 0;
  }

  public List<Integer> readPits(final String gameId) {
    return jdbcTemplate.query(
        "select size from pit where gameId = ? order by pitId",
        (rs, rowNum) -> rs.getInt(1),
        gameId);
  }

  public void updatePits(final String gameId, final List<Integer> pits) {
    final List<Object> values = new ArrayList<>();
    final StringJoiner updateSql =
        new StringJoiner("", "update pit set size = (case ", "end) where gameId = ?");
    for (int i = 0; i < pits.size(); i++) {
      updateSql.add("when pitId = ? then ? ");
      values.add(i);
      values.add(pits.get(i));
    }
    values.add(gameId);
    jdbcTemplate.update(updateSql.toString(), values.toArray());
  }

  private static Game gameRowMapper(final ResultSet rs, final int rowNum) throws SQLException {
    final String id = rs.getString(1);
    final Integer nextPlayer = rs.getObject(2, Integer.class);
    final boolean gameOver = rs.getBoolean(3);
    return new Game(id, Player.ofIndex(nextPlayer), gameOver);
  }

  private static Map<String, Object> gameToJdbcMap(final Game game) {
    final Map<String, Object> map = new HashMap<>();
    map.put("id", game.id());
    map.put("nextPlayer", game.nextPlayer());
    map.put("gameOver", game.gameOver());
    return map;
  }

  private static Map<String, Object> pitToJdbcMap(
      final String gameId, final int pitId, final int size) {
    return Map.of("gameId", gameId, "pitId", pitId, "size", size);
  }
}
