// Базовый URL REST-контроллера
const ADMIN_API_URL = '/api/v1/admin/users';
const USER_API_URL = '/api/v1/user';

// Главная точка входа: запускаем функции после загрузки страницы
document.addEventListener('DOMContentLoaded', () => {
    initNavbar();
    loadUsersTable();
    setupCreateForm();
    setupEditForm();
    setupDeleteForm();
});

// ========================================================
// 1. ЗАПОЛНЕНИЕ ШАПКИ (NAVBAR) И ВКЛАДКИ USER INFORMATION
// ========================================================
function initNavbar() {
    fetch(USER_API_URL)
        .then(res => res.json())
        .then(user => {
            // Находим и заполняем email и роли текущего админа в шапке
            document.getElementById('navbar-email').innerText = user.email;

            // Превращаем ["ROLE_ADMIN", "ROLE_USER"] в строку "ADMIN USER"
            const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(' ');
            document.getElementById('navbar-roles').innerText = `with roles: ${roles}`;

            // --- ДОБАВЛЕНО: Заполнение таблицы во вкладке User ---
            const currentUserTableBody = document.getElementById('current-user-table-body');
            if (currentUserTableBody) {
                currentUserTableBody.innerHTML = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${roles}</td>
                    </tr>
                `;
            }
        })
        .catch(err => console.error('Ошибка загрузки navbar и таблицы пользователя:', err));
}

// ==========================================
// 2. ВЫВОД ВСЕХ ПОЛЬЗОВАТЕЛЕЙ В ТАБЛИЦУ
// ==========================================
function loadUsersTable() {
    fetch(ADMIN_API_URL)
        .then(res => res.json())
        .then(users => {
            const tableBody = document.getElementById('all-users-table-body');
            tableBody.innerHTML = ''; // Очищаем старые данные

            if (users.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="8" class="text-center">Пользователи не найдены</td></tr>';
                return;
            }

            users.forEach(user => {
                const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(' ');

                const row = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${roles}</td>
                        <td>
                            <button class="btn btn-info btn-sm text-white" 
                                    onclick="openEditModal(${user.id})">Edit</button>
                        </td>
                        <td>
                            <button class="btn btn-danger btn-sm" 
                                    onclick="openDeleteModal(${user.id})">Delete</button>
                        </td>
                    </tr>
                `;
                tableBody.insertAdjacentHTML('beforeend', row);
            });
        })
        .catch(err => console.error('Ошибка загрузки таблицы:', err));
}

// ==========================================
// 3. ДОБАВЛЕНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ (POST)
// ==========================================
function setupCreateForm() {
    const form = document.getElementById('add-user-form');
    if (!form) return;

    form.addEventListener('submit', e => {
        e.preventDefault();

        const selectRoles = document.getElementById('add-user-roles');
        const roleIds = Array.from(selectRoles.selectedOptions).map(opt => parseInt(opt.value));

        // Вытаскиваем значения напрямую, принудительно убирая возможные пробелы
        const passwordValue = document.getElementById('add-user-password').value.trim();

        const userJson = {
            firstName: document.getElementById('add-user-firstname').value,
            lastName: document.getElementById('add-user-lastname').value,
            age: parseInt(document.getElementById('add-user-age').value),
            email: document.getElementById('add-user-email').value,
            username: document.getElementById('add-user-email').value,
            password: passwordValue // Наше проверенное значение
        };

        // ВЫВОДИМ В КОНСОЛЬ ДЛЯ ПРОВЕРКИ (Нажмите F12 в браузере перед отправкой!)
        console.log("Отправляем на бэкенд объект:", userJson);

        const params = new URLSearchParams();
        roleIds.forEach(id => params.append('roleIds', id));

        fetch(`${ADMIN_API_URL}?${params.toString()}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userJson)
        })
            .then(res => {
                if (res.ok) {
                    form.reset();
                    loadUsersTable();
                    const triggerEl = document.getElementById('users-table-tab');
                    if (triggerEl) triggerEl.click();
                } else {
                    alert('Не удалось создать пользователя. Проверьте консоль бэкенда.');
                }
            })
            .catch(err => console.error('Ошибка при создании:', err));
    });
}

// ==========================================
// 4. МОДАЛЬНОЕ ОКНО РЕДАКТИРОВАНИЯ (EDIT)
// ==========================================
function openEditModal(id) {
    // Получаем актуальные данные пользователя с бэкенда и заполняем форму модалки
    fetch(`${ADMIN_API_URL}/${id}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('edit-id').value = user.id;
            document.getElementById('edit-firstname').value = user.firstName;
            document.getElementById('edit-lastname').value = user.lastName;
            document.getElementById('edit-age').value = user.age;
            document.getElementById('edit-email').value = user.email;
            document.getElementById('edit-password').value = ''; // Пароль оставляем пустым для безопасности

            // Выделяем текущие роли пользователя в селекте модалки
            const selectRoles = document.getElementById('edit-roles');
            const userRoleIds = user.roles.map(r => r.id);
            Array.from(selectRoles.options).forEach(opt => {
                opt.selected = userRoleIds.includes(parseInt(opt.value));
            });

            // Показываем модальное окно с помощью встроенного API Bootstrap 5
            const editModal = new bootstrap.Modal(document.getElementById('editModal'));
            editModal.show();
        })
        .catch(err => console.error('Ошибка открытия модального окна редактирования:', err));
}

function setupEditForm() {
    const form = document.getElementById('edit-user-form');
    if (!form) return;

    form.addEventListener('submit', e => {
        e.preventDefault();

        const id = document.getElementById('edit-id').value;
        const selectRoles = document.getElementById('edit-roles');
        const roleIds = Array.from(selectRoles.selectedOptions).map(opt => parseInt(opt.value));

        const userJson = {
            firstName: document.getElementById('edit-firstname').value,
            lastName: document.getElementById('edit-lastname').value,
            age: parseInt(document.getElementById('edit-age').value),
            email: document.getElementById('edit-email').value,
            username: document.getElementById('edit-email').value,
            password: document.getElementById('edit-password').value
        };

        const params = new URLSearchParams();
        roleIds.forEach(id => params.append('roleIds', id));

        fetch(`${ADMIN_API_URL}/${id}?${params.toString()}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userJson)
        })
            .then(res => {
                if (res.ok) {
                    const closeButton = document.querySelector('#editModal [data-bs-dismiss="modal"]')
                        || document.querySelector('#editModal [data-dismiss="modal"]');
                    if (closeButton) {
                        closeButton.click();
                    }

                    loadUsersTable(); // Перерисовываем таблицу с новыми данными
                } else {
                    alert('Ошибка обновления данных');
                }
            })
            .catch(err => console.error('Ошибка отправки PUT запроса:', err));
    });
}

// ==========================================
// 5. МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ (DELETE)
// ==========================================
function openDeleteModal(id) {
    fetch(`${ADMIN_API_URL}/${id}`)
        .then(res => res.json())
        .then(user => {
            // Заполняем поля модалки (в HTML они должны быть disabled)
            document.getElementById('delete-id').value = user.id;
            document.getElementById('delete-firstname').value = user.firstName;
            document.getElementById('delete-lastname').value = user.lastName;
            document.getElementById('delete-age').value = user.age;
            document.getElementById('delete-email').value = user.email;

            const selectRoles = document.getElementById('delete-roles');
            const userRoleIds = user.roles.map(r => r.id);
            Array.from(selectRoles.options).forEach(opt => {
                opt.selected = userRoleIds.includes(parseInt(opt.value));
            });

            // Показываем модальное окно удаления
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        })
        .catch(err => console.error('Ошибка открытия модального окна удаления:', err));
}

function setupDeleteForm() {
    const form = document.getElementById('delete-user-form');
    if (!form) return;

    form.addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('delete-id').value;

        fetch(`${ADMIN_API_URL}/${id}`, {
            method: 'DELETE'
        })
            .then(res => {
                if (res.ok) {
                    const closeButton = document.querySelector('#deleteModal [data-bs-dismiss="modal"]')
                        || document.querySelector('#deleteModal [data-dismiss="modal"]')
                        || document.querySelector('#deleteModal .btn-secondary'); // Запасной вариант по классу кнопки Close

                    if (closeButton) {
                        closeButton.click(); // Симулируем клик для закрытия
                    }

                    loadUsersTable(); // Обновляем таблицу (удаленный юзер исчезнет)
                } else {
                    alert('Не удалось удалить пользователя');
                }
            })
            .catch(err => console.error('Ошибка отправки DELETE запроса:', err));
    });
}

