package ru.kata.spring.boot_rest.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_rest.demo.dao.RoleDao;
import ru.kata.spring.boot_rest.demo.model.Role;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.getAllRoles();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleDao.getRoleById(id);
    }

    @Override
    public Set<Role> getRolesByIds(Set<Long> roleIds) {
        // Проверка на null и пустоту
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Role IDs cannot be null or empty");
        }

        // ОДИН запрос к БД для получения ВСЕХ ролей
        return roleDao.getRolesByIds(roleIds);
    }

    @Override
    public void saveRole(Role role) {
        roleDao.saveRole(role);
    }
}
