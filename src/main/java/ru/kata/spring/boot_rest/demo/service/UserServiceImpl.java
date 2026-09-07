package ru.kata.spring.boot_rest.demo.service;

import org.springframework.beans.BeanUtils;
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

//    @Override
//    public void updateUser(User user, Set<Long> roleIds) {
//        User existingUser = getUserById(user.getId());
//
//        // Копируем все свойства, игнорируя null
//        BeanUtils.copyProperties(user, existingUser,
//                "id", "password", "roles", "authorities");
//
//        // Обновляем пароль, если он был изменен
//        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
//            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
//        }
//
//        // Обновляем роли
//        Set<Role> roles = roleService.getRolesByIds(roleIds);
//        existingUser.setRoles(roles);
//
//        userDao.updateUser(existingUser);
//    }

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
    public void updateUser(Long id, User user, Set<Long> roleIds) {
        // 1. Извлекаем текущего пользователя из БД
        User existingUser = getUserById(id);

        // 2. Явно обновляем текстовые/числовые поля без рефлексии (BeanUtils)
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        existingUser.setUsername(user.getUsername());

        // 3. Обновляем пароль только в том случае, если с фронтенда пришел новый пароль
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 4. Загружаем и сетим новые роли
        Set<Role> roles = roleService.getRolesByIds(roleIds);
        existingUser.setRoles(roles);

        // 5. Передаем подготовленный объект в DAO
        userDao.updateUser(existingUser);
    }
}
