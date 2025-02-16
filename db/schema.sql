CREATE TABLE IF NOT EXISTS game (
    game_id INTEGER IDENTITY PRIMARY KEY,
    player1 VARCHAR(50),
    player2 VARCHAR(50),
    winner VARCHAR(50),
    date_time TIMESTAMP
)
