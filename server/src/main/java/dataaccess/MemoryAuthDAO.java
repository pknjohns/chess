package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    // key here is authToken (string)
    final private HashMap<String, AuthData> tokens = new HashMap<>();

    public AuthData getAuth(String authToken) {
        return tokens.get(authToken);
    }

    public AuthData addAuth(AuthData auth) {
        tokens.put(auth.authToken(), auth);
        return auth;
    }

    public void deleteAuth(String authToken) {
        tokens.remove(authToken);
    }

    public Collection<AuthData> listAuths() {
        return tokens.values();
    }

    public void deleteAllAuths() {
        tokens.clear();
    }
}
