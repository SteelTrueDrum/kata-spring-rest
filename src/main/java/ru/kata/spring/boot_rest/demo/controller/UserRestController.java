package ru.kata.spring.boot_rest.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_rest.demo.model.User;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    // GET /api/v1/user - получить данные текущего авторизованного пользователя
    @GetMapping
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User authUser) {
        // Аннотация @AuthenticationPrincipal автоматически достает объект User из SecurityContext.
        return ResponseEntity.ok(authUser);
    }
}
