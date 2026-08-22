package ru.kata.spring.boot_rest.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_rest.demo.model.User;
import ru.kata.spring.boot_rest.demo.service.RoleService;
import ru.kata.spring.boot_rest.demo.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // GET /admin - список всех пользователей
    @GetMapping
    public String listUsers(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("user", user);
        model.addAttribute("newUser", new User());
        model.addAttribute("editUser", new User());
        model.addAttribute("deleteUser", new User());
        return "admin";
    }

    // POST /admin - сохранение пользователя
    @PostMapping("/new")
    public String saveUser(@ModelAttribute("newUser") User user,
                           @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {

        userService.saveUser(user, roleIds);
        return "redirect:/admin";
    }

    // PUT /admin/{id} - обновление пользователя
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User user,
                             @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        user.setId(id);
        userService.updateUser(user, roleIds);
        return "redirect:/admin";
    }

    // DELETE /admin/{id} - удаление пользователя
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
