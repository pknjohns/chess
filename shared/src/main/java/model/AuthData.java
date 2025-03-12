package model;

import com.google.gson.Gson;

public record AuthData(
        String authToken,
        String username
) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
