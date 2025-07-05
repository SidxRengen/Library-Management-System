package main.java.com.library.dao;

import main.java.com.library.models.User;
import java.util.List;

public interface UserDao {
    User addUser(User user);
    User updateUser(User user);
    void deleteUser(String userId);
    User findUserById(String userId);
    List<User> findAllUsers();
    List<User> findUsersByName(String name);
    List<User> findUsersByType(User.UserType userType);
}