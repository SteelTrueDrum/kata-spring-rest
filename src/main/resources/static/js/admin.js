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

// Helper-функция для форматирования ролей из объекта (убирает ROLE_)
function formatRoles(rolesArray) {
    return rolesArray.map(r => r.name.replace('ROLE_', '')).join(' ');
}

// Helper-функция для генерации HTML-строки пользователя
function createUserRowHtml(user) {
    const roles = formatRoles(user.roles);
    return `
        <tr id="user-row-${user.id}">
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
}

// ========================================================
// 1. ЗАПОЛНЕНИЕ ШАПКИ (NAVBAR) И ВКЛАДКИ USER INFORMATION
// ========================================================
function initNavbar() {
    fetch(USER_API_URL)
        .then(res => res.json())
        .then(user => {
            document.getElementById('navbar-email').innerText = user.email;

            const roles = formatRoles(user.roles);
            document.getElementById('navbar-roles').innerText = `with roles: ${roles}`;

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
            tableBody.innerHTML = '';

            if (users.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="8" class="text-center">Пользователи не найдены</td></tr>';
                return;
            }

            users.forEach(user => {
                tableBody.insertAdjacentHTML('beforeend', createUserRowHtml(user));
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
        const passwordValue = document.getElementById('add-user-password').value.trim();

        const userJson = {
            firstName: document.getElementById('add-user-firstname').value,
            lastName: document.getElementById('add-user-lastname').value,
            age: parseInt(document.getElementById('add-user-age').value),
            email: document.getElementById('add-user-email').value,
            username: document.getElementById('add-user-email').value,
            password: passwordValue
        };

        const params = new URLSearchParams();
        roleIds.forEach(id => params.append('roleIds', id));

        fetch(`${ADMIN_API_URL}?${params.toString()}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userJson)
        })
            .then(res => {
                if (!res.ok) throw new Error('Ошибка при создании пользователя');
                return res.json(); // Принимаем чистый объект User с бэкенда (статус 201)
            })
            .then(createdUser => {
                form.reset();

                // ООП Подход: Вместо перезагрузки всей таблицы loadUsersTable(),
                // мы просто точечно вставляем новую строку в конец таблицы!
                const tableBody = document.getElementById('all-users-table-body');
                // Если таблица была пуста, убираем заглушку
                if (tableBody.innerHTML.includes('Пользователи не найдены')) {
                    tableBody.innerHTML = '';
                }
                tableBody.insertAdjacentHTML('beforeend', createUserRowHtml(createdUser));

                // Переключаем вкладку на таблицу пользователей
                const triggerEl = document.getElementById('users-table-tab');
                if (triggerEl) triggerEl.click();
            })
            .catch(err => {
                console.error('Ошибка при создании:', err);
                alert('Не удалось создать пользователя.');
            });
    });
}

// ==========================================
// 4. МОДАЛЬНОЕ ОКНО РЕДАКТИРОВАНИЯ (EDIT)
// ==========================================
function openEditModal(id) {
    fetch(`${ADMIN_API_URL}/${id}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('edit-id').value = user.id;
            document.getElementById('edit-firstname').value = user.firstName;
            document.getElementById('edit-lastname').value = user.lastName;
            document.getElementById('edit-age').value = user.age;
            document.getElementById('edit-email').value = user.email;
            document.getElementById('edit-password').value = '';

            const selectRoles = document.getElementById('edit-roles');
            const userRoleIds = user.roles.map(r => r.id);
            Array.from(selectRoles.options).forEach(opt => {
                opt.selected = userRoleIds.includes(parseInt(opt.value));
            });

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
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userJson)
        })
            .then(res => {
                if (!res.ok) throw new Error('Ошибка обновления данных');
                return res.json(); // Получаем обновленный чистый объект User (статус 200)
            })
            .then(updatedUser => {
                // Закрываем модалку через Close-кнопку
                const closeButton = document.querySelector('#editModal [data-bs-dismiss="modal"]')
                    || document.querySelector('#editModal [data-dismiss="modal"]');
                if (closeButton) closeButton.click();

                // ООП Подход: Вместо полной перезагрузки таблицы, мы находим
                // конкретную строку по ID (благодаря добавленному id="user-row-...") и заменяем её
                const oldRow = document.getElementById(`user-row-${updatedUser.id}`);
                if (oldRow) {
                    oldRow.outerHTML = createUserRowHtml(updatedUser);
                }
            })
            .catch(err => {
                console.error('Ошибка отправки PUT запроса:', err);
                alert('Ошибка обновления данных');
            });
    });
}

// ==========================================
// 5. МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ (DELETE)
// ==========================================
function openDeleteModal(id) {
    fetch(`${ADMIN_API_URL}/${id}`)
        .then(res => res.json())
        .then(user => {
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

            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        })
        .catch(err => console.error('Ошибка открытия модалки удаления:', err));
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
                if (res.ok) { // Ожидаем статус 204 No Content без тела
                    const closeButton = document.querySelector('#deleteModal [data-bs-dismiss="modal"]')
                        || document.querySelector('#deleteModal [data-dismiss="modal"]')
                        || document.querySelector('#deleteModal .btn-secondary');

                    if (closeButton) closeButton.click();

                    // Находим строку в DOM по уникальному ID и удаляем ее из дерева
                    const rowToDelete = document.getElementById(`user-row-${id}`);
                    if (rowToDelete) {
                        rowToDelete.remove();
                    }
                } else {
                    throw new Error('Не удалось удалить пользователя');
                }
            })
            .catch(err => {
                console.error('Ошибка отправки DELETE запроса:', err);
                alert('Не удалось удалить пользователя');
            });
    });
}