package ru.kata.spring.boot_rest.demo.service;

import ru.kata.spring.boot_rest.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void saveUser(User user, Set<Long> roleIds);
    void updateUser(User user, Set<Long> roleIds);
    void deleteUser(Long id);

    // Методы по умолчанию для одного параметра User
    default void saveUser(User user) {
        saveUser(user, null);
    }

    default void updateUser(User user) {
        updateUser(user, null);
    }

    User findByEmail(String name);
}
