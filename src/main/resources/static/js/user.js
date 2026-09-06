// 1. Указываем адрес нашего REST-контроллера
const USER_API_URL = '/api/v1/user';

// 2. Ждем, пока браузер полностью загрузит HTML-страницу, и только тогда запускаем код
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUserData();
});

// 3. Основная функция, которая идет на бэкенд за данными и вставляет их в HTML
function fetchAndRenderUserData() {

    // Делаем асинхронный запрос к UserRestController
    fetch(USER_API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось получить данные пользователя');
            }
            return response.json(); // Превращаем JSON-ответ от сервера в JS-объект
        })
        .then(user => {
            // ---- А. ЗАПОЛНЯЕМ ШАПКУ СТРАНИЦЫ (NAVBAR) ----
            document.getElementById('navbar-email').innerText = user.email;

            // Преобразуем список ролей в строку через пробел (убираем префикс ROLE_)
            const rolesString = user.roles.map(role => role.name.replace('ROLE_', '')).join(' ');
            document.getElementById('navbar-roles').innerText = `with roles: ${rolesString}`;

            // ---- Б. ЗАПОЛНЯЕМ ТАБЛИЦУ С ДАННЫМИ ПОЛЬЗОВАТЕЛЯ ----
            const tableBody = document.getElementById('user-table-body');

            // Создаем HTML-строку, подставляя переменные из объекта 'user'
            const rowHtml = `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${rolesString}</td>
                </tr>
            `;

            // Заменяем текст "Загрузка..." на нашу готовую строку таблицы
            tableBody.innerHTML = rowHtml;
        })
        .catch(error => {
            console.error('Ошибка Fetch:', error);
            // Если что-то пошло не так (например, сессия истекла), выведем ошибку в таблицу
            document.getElementById('user-table-body').innerHTML =
                `<tr><td colspan="6" class="text-center text-danger">Ошибка загрузки данных</td></tr>`;
        });
}