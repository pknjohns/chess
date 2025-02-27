package model;

import com.google.gson.*;

import java.util.Objects;

public record UserData(
        String username,
        String password,
        String email) {

    public UserData setUsername(String username) {
        return new UserData(username, this.password, this.email);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
