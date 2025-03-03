package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    // key here is authToken (string)
    final private HashMap<String, AuthData> tokens = new HashMap<>();

    public AuthData addToken(AuthData token) {
        tokens.put(token.authToken(), token);
        return token;
    }

    public Collection<AuthData> listTokens() {
        return tokens.values();
    }

    public void deleteAllTokens() {
        tokens.clear();
    }
}
