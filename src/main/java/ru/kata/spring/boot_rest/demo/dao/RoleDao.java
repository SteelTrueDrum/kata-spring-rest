package ru.kata.spring.boot_rest.demo.dao;

import ru.kata.spring.boot_rest.demo.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleDao {
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    void saveRole(Role role);
    Set<Role> getRolesByIds(Set<Long> roleIds);
}
