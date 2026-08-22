package ru.kata.spring.boot_rest.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_rest.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RoleDaoImpl implements RoleDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> getAllRoles() {
        return entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    @Override
    public Role getRoleById(Long id) {
        return entityManager.find(Role.class, id);
    }

    @Override
    public void saveRole(Role role) {
        if (role.getId() == null) {
            entityManager.persist(role);
        } else {
            entityManager.merge(role);
        }
    }

    @Override
    public Set<Role> getRolesByIds(Set<Long> roleIds) {
        return entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.id IN :roleIds", Role.class)
                .setParameter("roleIds", roleIds)
                .getResultStream()
                .collect(Collectors.toSet());
    }
}
