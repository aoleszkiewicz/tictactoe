package org.tictactoe.dao;

import org.tictactoe.datasource.Datasource;
import org.tictactoe.interfaces.IGameDAO;
import org.tictactoe.interfaces.IGame;
import org.tictactoe.model.Game;
import org.tictactoe.model.Player;
import org.tictactoe.model.Winner;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDAO implements IGameDAO {

    @Override
    public List<IGame> findAll() {
        String query = "SELECT * FROM game ORDER BY date_time DESC";
        List<IGame> games = new ArrayList<IGame>();

        try (Connection connection = Datasource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int gameId = resultSet.getInt("game_id");
                    String player1Name = resultSet.getString("player1");
                    String player2Name = resultSet.getString("player2");
                    String winnerName = resultSet.getString("winner");
                    LocalDateTime dateTime = resultSet.getTimestamp("date_time").toLocalDateTime();
                    Game game = new Game(gameId, new Player(player1Name), new Player(player2Name), new Winner(winnerName), dateTime);
                    games.add(game);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    @Override
    public void save(IGame game) {
        String query = "INSERT INTO game (player1, player2, winner, date_time) VALUES (?, ?, ?, ?)";

        String player1Name = game.getPlayer1().getName();
        String player2Name = game.getPlayer2().getName();
        String winnerName = game.getWinner().getName();

        try (Connection connection = Datasource.getConnection(); PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, player1Name);
            statement.setString(2, player2Name);
            statement.setString(3, winnerName);
            statement.setTimestamp(4, Timestamp.valueOf(game.getTime()));
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                game.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM game WHERE game_id = ?";

        try (Connection connection = Datasource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
