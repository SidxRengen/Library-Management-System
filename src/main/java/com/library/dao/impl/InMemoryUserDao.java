package main.java.com.library.dao.impl;

import main.java.com.library.dao.UserDao;
import main.java.com.library.models.User;
import java.util.List;
import main.java.com.library.dao.UserDao;
import main.java.com.library.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryUserDao implements UserDao {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getUserId())) {
            return null;
        }
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public void deleteUser(String userId) {
        users.remove(userId);
    }

    @Override
    public User findUserById(String userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findUsersByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByType(User.UserType userType) {
        return users.values().stream()
                .filter(user -> user.getUserType() == userType)
                .collect(Collectors.toList());
    }
}