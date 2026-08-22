package ru.kata.spring.boot_rest.demo.dao;

import ru.kata.spring.boot_rest.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> getAllUsers();

    void saveUser(User user);

    User getUserById(Long id);

    void updateUser(User user);

    void deleteUser(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
