package org.tictactoe.interfaces;

import java.util.List;

public interface IGameDAO {
    List<IGame> findAll();
    void save(IGame game);
    void delete(int id);
}
