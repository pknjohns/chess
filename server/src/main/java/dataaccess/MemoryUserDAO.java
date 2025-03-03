package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // key in this case is username (string)
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData addUser(UserData user) {
        users.put(user.username(), user);
        return user;
    }

    public Collection<UserData> listUsers() {
        return users.values();
    }

    public void deleteAllUsers() {
        users.clear();
    }
}
