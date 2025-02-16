package org.tictactoe.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoadSQL {
    public static String loadSQLFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading SQL file: " + filePath, e);
        }
    }
}
