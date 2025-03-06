package model;

public record ListGameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {}
