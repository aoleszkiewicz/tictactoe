package com.example.oxgame;

import com.example.engine.OXEnum;
import com.example.engine.OXGame;
import com.example.engine.OXGameImpl;
import com.example.model.Game;
import com.example.dao.GameDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.datasource.DataSource;

public class MainController {
    @FXML
    private TableView<Game> gameTable;
    @FXML
    private TableColumn<Game, Integer> gameIdColumn;
    @FXML
    private TableColumn<Game, String> playerXColumn;
    @FXML
    private TableColumn<Game, String> playerOColumn;
    @FXML
    private TableColumn<Game, OXEnum> winnerColumn;
    @FXML
    private TableColumn<Game, LocalDateTime> gameDateTimeColumn;
    @FXML
    private Button playButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField playerXTextField;
    @FXML
    private TextField playerOTextField;
    @FXML
    private Button fieldButton0;
    @FXML
    private Button fieldButton1;
    @FXML
    private Button fieldButton2;
    @FXML
    private Button fieldButton3;
    @FXML
    private Button fieldButton4;
    @FXML
    private Button fieldButton5;
    @FXML
    private Button fieldButton6;
    @FXML
    private Button fieldButton7;
    @FXML
    private Button fieldButton8;

    private OXGame game;
    private ObservableList<Game> history;
    private GameDAOImpl gameDAO;

    private static final int MAX_HISTORY_ENTRIES = 10;

    @FXML
    private void initialize() {
        try {
            // utworz tabele jezeli nie istnieje
            try (Connection connection = DataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS game (" +
                    "game_id INTEGER IDENTITY PRIMARY KEY, " +
                    "player_x VARCHAR(50), " +
                    "player_o VARCHAR(50), " +
                    "winner VARCHAR(1), " +
                    "date_time TIMESTAMP)"
                );
            }

            gameDAO = new GameDAOImpl();

            // 1. POWIĄZANIE KOLUMN Z POLAMI KLASY GRY
            gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
            playerOColumn.setCellValueFactory(new PropertyValueFactory<>("playerO"));
            playerXColumn.setCellValueFactory(new PropertyValueFactory<>("playerX"));
            winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winner"));
            gameDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("gameDateTime"));

            // 2. UTWORZENIE LISTY OBSERWOWALNEJ I JEJ POWIĄZANIE Z TABELĄ
            history = FXCollections.observableArrayList();
            gameTable.setItems(history);

            game = new OXGameImpl();  // inicjalizacja nowej gry

            List<Game> gamesFromDb = gameDAO.getGames(0, MAX_HISTORY_ENTRIES);
            history.addAll(gamesFromDb);

            // dodaj obsluge przyciskow play i wyczysc historie
            playButton.setOnAction(event -> startGame());
            clearButton.setOnAction(event -> clearHistory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startGame() {
        // inicjalizacja gry
        game.initialize();

        // aktywacja przycisków
        for (int i = 0; i <= 8; i++) {
            Button button = getButtonByIndex(i);
            button.setDisable(false);
            button.setText("");  // wyczyść tekst przycisku
            button.setOnAction(event -> handleButtonClick(button));
        }
    }

    private void handleButtonClick(Button button) {
        int index = Integer.parseInt(button.getId().substring(button.getId().length() - 1));

        // ustaw tekst o większej czcionce i pogrubiony
        button.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");
        button.setText(game.getTurn().toString());
        game.setField(index);
        button.setDisable(true);

        // sprawdź, czy gra się zakończyła
        checkGameEnd();
    }

    private void checkGameEnd() {
        OXEnum winner = game.getWinner();

        // jeżeli jest zwycięzca, zablokuj przyciski
        if (winner != OXEnum.NONE) {
            disableButtons();
            addGameToHistory(winner);
            System.out.println(winner);
        } else if (game.getStep() == 9) {
            // jeżeli to był ostatni ruch (9 kroków), to jest remis
            disableButtons();
            addGameToHistory(OXEnum.NONE);
        }
    }

    private void addGameToHistory(OXEnum winner) {
        String playerX = playerXTextField.getText();
        String playerO = playerOTextField.getText();
        LocalDateTime dateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        Game finishedGame = new Game(0, playerX, playerO, winner, LocalDateTime.parse(formattedDateTime, formatter));

        gameDAO.saveGame(finishedGame); // zapisz grę do bazy danych

        history.add(finishedGame);
    }

    private void disableButtons() {
        // zablokuj przyciski
        for (int i = 0; i <= 8; i++) {
            Button button = getButtonByIndex(i);
            button.setDisable(true);
        }
    }

    private void clearHistory() {
        // usuń gry z bazy danych i z listy historii
        for (Game game : history) {
            gameDAO.deleteGame(game.getGameId());
        }
        history.clear();
    }

    private Button getButtonByIndex(int index) {
        return switch (index) {
            case 0 -> fieldButton0;
            case 1 -> fieldButton1;
            case 2 -> fieldButton2;
            case 3 -> fieldButton3;
            case 4 -> fieldButton4;
            case 5 -> fieldButton5;
            case 6 -> fieldButton6;
            case 7 -> fieldButton7;
            case 8 -> fieldButton8;
            default -> throw new IllegalArgumentException("Nieprawidłowy indeks przycisku: " + index);
        };
    }
}
