CREATE TABLE IF NOT EXISTS game (
    game_id INTEGER IDENTITY PRIMARY KEY,
    player_x VARCHAR(50),
    player_o VARCHAR(50),
    winner VARCHAR(1),
    date_time TIMESTAMP
); 