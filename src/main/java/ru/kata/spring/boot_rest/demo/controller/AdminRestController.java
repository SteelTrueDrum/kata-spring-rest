package ru.kata.spring.boot_rest.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_rest.demo.model.User;
import ru.kata.spring.boot_rest.demo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {
    private final UserService userService;

    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/v1/admin/users - получить всех пользователей
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/v1/admin/users/{id} - получить пользователя по ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // POST /api/v1/admin/users - создать нового пользователя
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody User user,
            @RequestParam(required = false) Set<Long> roleIds) {

            userService.saveUser(user, roleIds);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("user", user);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // PUT /api/v1/admin/users/{id} - обновить пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            @RequestParam(required = false) Set<Long> roleIds) {
        userService.updateUser(id, user, roleIds);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    // DELETE /api/v1/admin/users/{id} - удалить пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

}
