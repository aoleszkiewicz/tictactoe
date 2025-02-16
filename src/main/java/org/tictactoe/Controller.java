package org.tictactoe;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import org.tictactoe.dao.GameDAO;
import org.tictactoe.datasource.Datasource;
import org.tictactoe.enums.OX;
import org.tictactoe.interfaces.IGame;
import org.tictactoe.interfaces.IGameDAO;
import org.tictactoe.interfaces.ITicTacToe;
import org.tictactoe.model.Game;
import org.tictactoe.model.Player;
import org.tictactoe.model.TicTacToe;
import org.tictactoe.model.Winner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {

    @FXML
    TableView<IGame> gameTable;

    @FXML
    TableColumn<IGame, Integer> gameIdColumn;

    @FXML
    TableColumn<IGame, String> player1NameColumn;

    @FXML
    TableColumn<IGame, String> player2NameColumn;

    @FXML
    TableColumn<IGame, String> winnerNameColumn;

    @FXML
    TableColumn<IGame, LocalDateTime> gameDateTimeColumn;

    @FXML
    Button playButton;

    @FXML
    Button clearButton;

    @FXML
    TextField player1TextField;

    @FXML
    TextField player2TextField;

    @FXML
    Button fieldButton0;

    @FXML
    Button fieldButton1;

    @FXML
    Button fieldButton2;

    @FXML
    Button fieldButton3;

    @FXML
    Button fieldButton4;

    @FXML
    Button fieldButton5;

    @FXML
    Button fieldButton6;

    @FXML
    Button fieldButton7;

    @FXML
    Button fieldButton8;

    ITicTacToe ticTacToe;
    ObservableList<IGame> games;
    IGameDAO gameDAO;

    @FXML
    private void initialize() {
        try {
            try (Connection connection = Datasource.getConnection(); Statement statement = connection.createStatement()) {
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS game ("
                        + "game_id INTEGER IDENTITY PRIMARY KEY, "
                        + "player1 VARCHAR(50), "
                        + "player2 VARCHAR(50), "
                        + "winner VARCHAR(50), "
                        + "date_time TIMESTAMP)"
                );
            }

            gameDAO = new GameDAO();

            gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            player1NameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlayer1().getName()));
            player2NameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlayer2().getName()));
            winnerNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getWinner().getName()));
            gameDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

            games = FXCollections.observableArrayList();
            gameTable.setItems(games);

            ticTacToe = new TicTacToe();

            List<IGame> gamesFromDb = gameDAO.findAll();
            games.addAll(gamesFromDb);

            playButton.setOnAction(event -> startGame());
            clearButton.setOnAction(event -> clearHistory());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void startGame() {
        ticTacToe.init();

        for (int i = 0; i <= 8; i++) {
            Button button = getButtonByIndex(i);
            button.setDisable(false);
            button.setText("");
            button.setOnAction(event -> handleButtonClick(button));
        }
    }

    private void handleButtonClick(Button button) {
        int index = Integer.parseInt(button.getId().substring(button.getId().length() - 1));

        button.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");
        button.setText(ticTacToe.getTurn().toString());
        ticTacToe.setField(index);
        button.setDisable(true);

        checkGameEnd();
    }

    private void checkGameEnd() {
        OX winner = ticTacToe.getWinner();

        if (winner != OX.EMPTY) {
            disableButtons();
            addGameToHistory(winner);
            System.out.println(winner);
        } else if (ticTacToe.getStep() == 9) {
            disableButtons();
            addGameToHistory(OX.EMPTY);
        }
    }

    private void addGameToHistory(OX winner) {
        String player1 = player1TextField.getText();
        String player2 = player2TextField.getText();
        LocalDateTime dateTime = LocalDateTime.now();

        IGame finishedGame = new Game(0, new Player(player1), new Player(player2),
                new Winner(winner == OX.X ? player1 : player2), dateTime);

        gameDAO.save(finishedGame);
        games.add(finishedGame);
    }

    private void disableButtons() {
        for (int i = 0; i <= 8; i++) {
            Button button = getButtonByIndex(i);
            button.setDisable(true);
        }
    }

    private void clearHistory() {
        for (IGame game : games) {
            gameDAO.delete(game.getId());
        }
        games.clear();
    }

    private Button getButtonByIndex(int index) {
        return switch (index) {
            case 0 ->
                fieldButton0;
            case 1 ->
                fieldButton1;
            case 2 ->
                fieldButton2;
            case 3 ->
                fieldButton3;
            case 4 ->
                fieldButton4;
            case 5 ->
                fieldButton5;
            case 6 ->
                fieldButton6;
            case 7 ->
                fieldButton7;
            case 8 ->
                fieldButton8;
            default ->
                throw new IllegalArgumentException("Invalid button index: " + index);
        };
    }
}
