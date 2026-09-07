package ru.kata.spring.boot_rest.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_rest.demo.dao.UserDao;
import ru.kata.spring.boot_rest.demo.model.Role;
import ru.kata.spring.boot_rest.demo.model.User;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public void saveUser(User user, Set<Long> roleIds) {

        Set<Role> roles = roleService.getRolesByIds(roleIds);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.saveUser(user);

    }

    @Override
    public User getUserById(Long id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        User user = userDao.getUserById(id);
        if (user != null) {
            userDao.deleteUser(user);
        }
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }

    @Override
    public void updateUser(Long id, User incomingUser, Set<Long> roleIds) {
        // 1. Привязываем ID, который пришел из пути (Path) контроллера
        incomingUser.setId(id);

        // 2. Обрабатываем пароль
        if (incomingUser.getPassword() != null && !incomingUser.getPassword().trim().isEmpty()) {
            // Если пришел новый пароль — шифруем его
            incomingUser.setPassword(passwordEncoder.encode(incomingUser.getPassword()));
        } else {
            // Если пароль пустой, берем старый пароль из базы, чтобы не затереть его null'ом
            User databaseUser = getUserById(id);
            incomingUser.setPassword(databaseUser.getPassword());
        }

        // 3. Загружаем из базы и сетим роли
        Set<Role> roles = roleService.getRolesByIds(roleIds);
        incomingUser.setRoles(roles);

        // 4. Передаем готовую модель напрямую в DAO
        userDao.updateUser(incomingUser);
    }
}
