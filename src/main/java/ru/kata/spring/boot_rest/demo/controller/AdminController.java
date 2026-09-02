package ru.kata.spring.boot_rest.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
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
    public ModelAndView listUsers(@AuthenticationPrincipal User user) {
        ModelAndView mav = new ModelAndView("admin");
        mav.addObject("users", userService.getAllUsers());
        mav.addObject("roles", roleService.getAllRoles());
        mav.addObject("user", user);
        mav.addObject("newUser", new User());
        return mav;
    }

    // POST /admin - сохранение пользователя
    @PostMapping("/new")
    public ModelAndView saveUser(@ModelAttribute("newUser") User user,
                                 @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        userService.saveUser(user, roleIds);
        return new ModelAndView("redirect:/admin");
    }

    // PUT /admin/{id} - обновление пользователя
    @PutMapping("/{id}")
    public ModelAndView updateUser(@PathVariable Long id,
                                   @ModelAttribute User user,
                                   @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        user.setId(id);
        userService.updateUser(user, roleIds);
        return new ModelAndView("redirect:/admin");
    }

    // DELETE /admin/{id} - удаление пользователя
    @DeleteMapping("/{id}")
    public ModelAndView deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ModelAndView("redirect:/admin");
    }
}
