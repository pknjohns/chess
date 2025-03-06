package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    // key here is authToken (string)
    final private HashMap<String, AuthData> tokens = new HashMap<>();

    public AuthData getAuth(String token) {
        return tokens.get(token);
    }

    public AuthData addAuth(AuthData auth) {
        tokens.put(auth.authToken(), auth);
        return auth;
    }

    public Collection<AuthData> listAuths() {
        return tokens.values();
    }

    public void deleteAllAuths() {
        tokens.clear();
    }
}
