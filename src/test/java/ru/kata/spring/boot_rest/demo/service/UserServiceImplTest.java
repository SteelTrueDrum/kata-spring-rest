package ru.kata.spring.boot_rest.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_rest.demo.dao.UserDao;
import ru.kata.spring.boot_rest.demo.model.Role;
import ru.kata.spring.boot_rest.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // создание заглушек
    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    // Создание тестируемого объекта
    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Set<Long> roleIds;
    private Set<Role> roles;

    // Выполняется перед каждым тестом (создание пользователя и заполнение его полей)
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setAge(25);

        roleIds = new HashSet<>();
        roleIds.add(1L);
        roleIds.add(2L);

        roles = new HashSet<>();
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        roles.add(role1);
        roles.add(role2);
    }

    // Пробный тест для проверки работы JUnit
    @Test
    void test() {
        assertTrue(true);
    }

    // Успешное получение списка всех пользователей
    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Given
        List<User> expectedUsers = List.of(testUser);
        when(userDao.getAllUsers()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = userService.getAllUsers();

        // Then
        assertThat(actualUsers)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(testUser);
        verify(userDao, times(1)).getAllUsers();
    }

    // проверка кодирования пароля и сохранение его
    @Test
    void saveUser_ShouldEncodePasswordAndSave() {
        // Given
        when(roleService.getRolesByIds(roleIds)).thenReturn(roles);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        doNothing().when(userDao).saveUser(testUser);

        // When
        userService.saveUser(testUser, roleIds);

        // Then
        assertThat(testUser.getRoles()).isEqualTo(roles);
        assertThat(testUser.getPassword()).isEqualTo("encoded_password");
        verify(roleService, times(1)).getRolesByIds(roleIds);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userDao, times(1)).saveUser(testUser);
    }

    // проверка получения пользователя если пользователь существует
    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userDao.getUserById(1L)).thenReturn(testUser);

        // When
        User actualUser = userService.getUserById(1L);

        // Then
        assertThat(actualUser)
                .isNotNull()
                .isEqualTo(testUser);
        verify(userDao, times(1)).getUserById(1L);
    }

    // проверка удаления пользователя, если пользователь существует
    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userDao.getUserById(1L)).thenReturn(testUser);
        doNothing().when(userDao).deleteUser(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userDao, times(1)).getUserById(1L);
        verify(userDao, times(1)).deleteUser(testUser);
    }

    // обновление пользователя с новым паролем
    @Test
    void updateUser_ShouldUpdateWithNewPassword() {
        // Given
        User updatedUser = new User();
        updatedUser.setPassword("newPass123");
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setFirstName("updated");
        updatedUser.setLastName("Name");

        when(roleService.getRolesByIds(roleIds)).thenReturn(roles);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded_new_password");
        doNothing().when(userDao).updateUser(updatedUser);

        // When
        userService.updateUser(1L, updatedUser, roleIds);

        // Then
        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getPassword()).isEqualTo("encoded_new_password");
        assertThat(updatedUser.getRoles()).isEqualTo(roles);
        verify(roleService, times(1)).getRolesByIds(roleIds);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userDao, times(1)).updateUser(updatedUser);
        verify(userDao, never()).getUserById(anyLong());
    }

    // при обновлении пользователя должен сохранять старый пароль, если передан пустой
    @Test
    void updateUser_ShouldKeepOldPassword_WhenNewPasswordIsEmpty() {
        // Given
        User updatedUser = new User();
        updatedUser.setPassword("");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");

        when(roleService.getRolesByIds(roleIds)).thenReturn(roles);
        when(userDao.getUserById(1L)).thenReturn(testUser);
        doNothing().when(userDao).updateUser(updatedUser);

        // When
        userService.updateUser(1L, updatedUser, roleIds);

        // Then
        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getPassword()).isEqualTo(testUser.getPassword());
        assertThat(updatedUser.getRoles()).isEqualTo(roles);
        verify(roleService, times(1)).getRolesByIds(roleIds);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userDao, times(1)).getUserById(1L);
        verify(userDao, times(1)).updateUser(updatedUser);
    }
}