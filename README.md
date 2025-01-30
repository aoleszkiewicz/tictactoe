## Cel projektu

Implementacja gry w kółko i krzyżyk z wykorzystaniem JavaFX oraz bazy danych do przechowywania historii rozgrywek.

## Uruchamianie
```bash
mvn clean javafx:run
```

## 1 Struktura projektu

### 1.1 Główne pakiety:

- `com.example.oxgame` - główny pakiet aplikacji
- `com.example.engine` - logika gry
- `com.example.model` - modele danych
- `com.example.dao` - warstwa dostępu do danych
- `com.example.datasource` - konfiguracja źródła danych

### 1.2 Kluczowe klasy:

1. `Main` - punkt wejścia aplikacji
2. `MainController` - kontroler głównego widoku
3. `Game` - model przechowujący dane o rozgrywce
4. `OXGame` - interfejs definiujący logikę gry
5. `GameDAO` - interfejs dostępu do bazy danych

## 1.3 Baza danych

### Tabela `game`:

- `game_id` - identyfikator gry (klucz główny)
- `player_x` - nazwa gracza X
- `player_o` - nazwa gracza O
- `winner` - zwycięzca (X/O/NONE)
- `date_time` - data i czas rozgrywki

## 1.4  Funkcjonalności

1. Rozgrywka na planszy 3x3
2. Wprowadzanie nazw graczy
3. Automatyczne wykrywanie zwycięzcy
4. Zapis historii gier do bazy danych
5. Wyświetlanie historii ostatnich 10 gier
6. Możliwość czyszczenia historii

## 2. Implemetancja w kodzie:

1. GameDAO - interfejs definiujacy operacje na danych:
    
    ```java
    package com.example.dao;
    
    import com.example.model.Game;
    
    import java.util.List;
    
    public interface GameDAO {
        void saveGame(Game game);
        List<Game> getGames(Integer fromRow, Integer numberOfRows);
        void deleteGame(int gameId);
    }
    
    ```
    
2. GameDAOImpl - implementacja `GameDAO` 
    
    ```java
    package com.example.dao;
    
    import com.example.engine.OXEnum;
    import com.example.model.Game;
    import com.example.datasource.DataSource;
    
    import java.sql.*;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;
    
    public class GameDAOImpl implements GameDAO {
    
        @Override
        public void saveGame(Game game) {
            String sql = "INSERT INTO game (player_o, player_x, winner, date_time) VALUES (?, ?, ?, ?)";
    
            try (Connection connection = DataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
                preparedStatement.setString(1, game.getPlayerX());
                preparedStatement.setString(2, game.getPlayerO());
                preparedStatement.setString(3, game.getWinner().toString());
    
                // formatowanie daty i czasu
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = game.getGameDateTime().format(formatter);
    
                preparedStatement.setString(4, formattedDateTime);
    
                preparedStatement.executeUpdate();
    
                // pobierz wygenerowane IDki
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int gameId = generatedKeys.getInt(1);
                    game.setGameId(gameId);
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        @Override
        public List<Game> getGames(Integer fromRow, Integer numberOfRows) {
            List<Game> games = new ArrayList<>();
            String selectQuery = "SELECT * FROM game ORDER BY date_time DESC LIMIT ? OFFSET ?";
    
            try (Connection connection = DataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
    
                preparedStatement.setInt(1, numberOfRows);
                preparedStatement.setInt(2, fromRow);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int gameId = resultSet.getInt("game_id");
                        String playerX = resultSet.getString("player_x");
                        String playerO = resultSet.getString("player_o");
                        String winner = resultSet.getString("winner");
                        LocalDateTime dateTime = resultSet.getTimestamp("date_time").toLocalDateTime();
                        OXEnum winnerEnum = OXEnum.fromString(winner);
                        Game game = new Game(gameId, playerX, playerO, winnerEnum, dateTime);
                        games.add(game);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            return games;
        }
    
        @Override
        public void deleteGame(int gameId) {
            String deleteQuery = "DELETE FROM game WHERE game_id = ?";
    
            try (Connection connection = DataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
    
                preparedStatement.setInt(1, gameId);
    
                int affectedRows = preparedStatement.executeUpdate();
    
                if (affectedRows == 0) {
                    throw new SQLException("Deleting game from database failed");
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    ```
    
3. DataSource, zarzadzanie polaczeniem z baza danych
    
    ```java
    package com.example.datasource;
    
    import java.sql.Connection;
    import java.sql.SQLException;
    import com.zaxxer.hikari.HikariConfig;
    import com.zaxxer.hikari.HikariDataSource;
    import java.io.File;
    
    public class DataSource {
        private static HikariConfig config;
        private static HikariDataSource ds;
        
        static {
            try {
                // sprawdz czy katalog db istnieje
                File dbDir = new File("db");
                if (!dbDir.exists()) {
                    dbDir.mkdirs();
                }
                
                config = new HikariConfig();
                config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
                config.setJdbcUrl("jdbc:hsqldb:file:db/oxgame;shutdown=true");
                config.setUsername("admin");
                config.setPassword("admin");
                config.setAutoCommit(true);
                ds = new HikariDataSource(config);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize database connection", e);
            }
        }
    
        private DataSource() {}
    
        public static Connection getConnection() throws SQLException {
            return ds.getConnection(); // zwroc buforwana liste polaczen aby uniknac korzystania z DriverManagera i uniknac tym samym tworzenie nowych polaczen przy otwarciu okienka
        }
    }
    
    ```
    
4. OXEnum,
    
    ```java
    package com.example.engine;
    
    public enum OXEnum {
        O("O"), X("X"), NONE("");
    
        private String str;
    
        private OXEnum(String str) {
            this.str = str;
        }
    
        @Override
        public String toString() {
            return str;
        }
    
        public static OXEnum fromString(String value) {
            if (value == null || value.isEmpty()) {
                return NONE;
            } else if (O.str.equalsIgnoreCase(value)) {
                return O;
            } else if (X.str.equalsIgnoreCase(value)) {
                return X;
            }
            return null;
        }
    }
    
    ```
    
5. OXGame, interfejs definiujacy logike gry
    
    ```java
    package com.example.engine;
    
    public interface OXGame {
            void initialize();
            void setField(int index);
            OXEnum getField(int index);
            OXEnum getTurn();
            OXEnum getWinner();
            int getStep();
    }
    
    ```
    
6. OXGameImpl, - implementacja interfejsu `OXGame`
    
    ```java
    package com.example.engine;
    
    import java.util.Arrays;
    
    public class OXGameImpl implements OXGame {
        private OXEnum[] board;
        private int step;
        private OXEnum turn;
        private OXEnum winner;
    
        @Override
        public void initialize() {
            board = new OXEnum[9];
            Arrays.fill(board, OXEnum.NONE);
            step = 0;
            turn = OXEnum.X;
            winner = OXEnum.NONE;
        }
    
        @Override
        public void setField(int index) {
            board[index] = turn;
            if (checkWinner() != OXEnum.NONE) {
                winner = turn;
            } else {
                turn = (turn == OXEnum.X) ? OXEnum.O : OXEnum.X;
                step++;
            }
        }
    
        @Override
        public OXEnum getField(int index) {
            return board[index];
        }
    
        @Override
        public OXEnum getTurn() {
            return turn;
        }
    
        @Override
        public OXEnum getWinner() {
            return winner;
        }
    
        @Override
        public int getStep() {
            return step;
        }
    
        private OXEnum checkWinner() {
            // sprawdz linie poziome
            for (int i = 0; i < 3; i++) {
                if (board[i * 3] != OXEnum.NONE &&
                        board[i * 3] == board[i * 3 + 1] &&
                        board[i * 3] == board[i * 3 + 2]) {
                    return getTurn();
                }
            }
    
            // sprawdz pionowe linie
            for (int i = 0; i < 3; i++) {
                if (board[i] != OXEnum.NONE &&
                        board[i] == board[i + 3] &&
                        board[i] == board[i + 6]) {
                    return getTurn();
                }
            }
    
            // sprwadz przekatne
            if (board[0] != OXEnum.NONE &&
                    board[0] == board[4] &&
                    board[0] == board[8]) {
                return getTurn();
            }
    
            if (board[2] != OXEnum.NONE &&
                    board[2] == board[4] &&
                    board[2] == board[6]) {
                return getTurn();
            }
    
            // sprawdz czy remis
            if (step == 9) {
                return OXEnum.NONE;
            }
    
            // Brak zwycięzcy
            return OXEnum.NONE;
        }
    }
    ```
    
7. Game, - klasa reprezentujaca pojedyncza rozgrywke w TicTacToe
    
    ```java
    package com.example.model;
    
    import com.example.engine.OXEnum;
    
    import java.time.LocalDateTime;
    
    public class Game {
        private Integer gameId;
        private String playerX;
        private String playerO;
        private OXEnum winner;
        private LocalDateTime gameDateTime;
    
        public Game() {
        }
    
        public Game(Integer gameId, String playerX, String playerO, OXEnum winner, LocalDateTime gameDateTime) {
            this.gameId = gameId;
            this.playerX = playerX;
            this.playerO = playerO;
            this.winner = winner;
            this.gameDateTime = gameDateTime;
        }
    
        public Integer getGameId() {
            return gameId;
        }
    
        public void setGameId(Integer gameId) {
            this.gameId = gameId;
        }
    
        public String getPlayerX() {
            return playerX;
        }
    
        public void setPlayerX(String playerX) {
            this.playerX = playerX;
        }
    
        public String getPlayerO() {
            return playerO;
        }
    
        public void setPlayerO(String playerO) {
            this.playerO = playerO;
        }
    
        public OXEnum getWinner() {
            return winner;
        }
    
        public void setWinner(OXEnum winner) {
            this.winner = winner;
        }
    
        public LocalDateTime getGameDateTime() {
            return gameDateTime;
        }
    
        public void setGameDateTime(LocalDateTime gameDateTime) {
            this.gameDateTime = gameDateTime;
        }
    }
    
    ```
    
8. Main,
    
    ```java
    package com.example.oxgame;
    
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.stage.Stage;
    
    import java.io.IOException;
    
    public class Main extends Application {
        @Override
        public void start(Stage stage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/oxgame/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 386, 567);
            stage.setTitle("Tic Tac Toe");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
    
        public static void main(String[] args) {
            launch();
        }
    }
    ```
    
9. MainController,
    
    ```java
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
    
    ```

    

# 3. Wnioski

- **Dobrze zorganizowana struktura**
    
    Projekt został podzielony na logiczne pakiety, co poprawia czytelność i ułatwia rozwój. Jasny podział na warstwę logiki (`engine`), model danych (`model`), oraz warstwę dostępu do bazy (`dao`) sprzyja modularności.
    
- **Poprawnie zaplanowana baza danych**
    
    Struktura tabeli `game` jest dobrze przemyślana – zawiera kluczowe informacje o rozgrywce.
    
- **Kluczowe funkcjonalności są dobrze określone**
    
    Gra posiada wszystkie podstawowe elementy, które powinna mieć każda implementacja gry TicTacToe – wykrywanie zwycięzcy, zapis historii i możliwość jej przeglądania.
    
- **Możliwość rozbudowy**
    
    Projekt ma potencjał do dalszego rozwoju. Można rozważyć:
    
    - **Obsługę większych plansz** (np. 5x5)
    - **Grę sieciową** (z wykorzystaniem WebSocket lub REST API)
