package ru.kata.spring.boot_rest.demo.service;

import ru.kata.spring.boot_rest.demo.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
        List<Role> getAllRoles();
        Role getRoleById(Long id);
        Set<Role> getRolesByIds(Set<Long> roleIds);
        void saveRole(Role role);
}
