<div align="center">
  <h1>Bank Card Management System</h1>
  <p>
    <span>REST API для управления банковскими картами, пользователями и авторизацией на базе</span>
    <code>Spring Boot</code>, <code>Spring Security</code>, <code>PostgreSQL</code>, <code>Liquibase</code>.
  </p>
</div>

<hr />

<div>
  <h2>О проекте</h2>
  <p>
    Приложение предоставляет API для регистрации и аутентификации пользователей,
    управления картами, фильтрации данных и переводов между картами.
  </p>
  <p>
    Базовый URL API: <code>http://localhost:8081/api/v1</code>
  </p>
</div>

<div>
  <h2>Технологический стек</h2>
  <ul>
    <li><span>Java</span> <code>21</code></li>
    <li><span>Spring Boot</span> <code>4.0.1</code></li>
    <li><span>Spring Security + JWT</span> (<code>jjwt 0.12.6</code>)</li>
    <li><span>Spring Data JPA</span></li>
    <li><span>Liquibase</span></li>
    <li><span>PostgreSQL</span></li>
    <li><span>Swagger / OpenAPI</span> (<code>springdoc-openapi</code>)</li>
    <li><span>Maven Wrapper</span> (<code>mvnw</code> / <code>mvnw.cmd</code>)</li>
  </ul>
</div>

<div>
  <h2>Основные эндпоинты</h2>
  <ul>
    <li><code>/api/v1/auth</code> — регистрация, вход, выход, refresh токена</li>
    <li><code>/api/v1/users</code> — управление пользователями</li>
    <li><code>/api/v1/private/cards</code> — приватные операции с картами</li>
    <li><code>/api/v1/public/cards</code> — публичные операции и переводы</li>
  </ul>
  <p>
    Swagger UI: <a href="http://localhost:8081/swagger-ui.html">http://localhost:8081/swagger-ui.html</a>
  </p>
</div>

<div>
  <h2>Переменные окружения</h2>
  <p>Для корректной работы необходимо задать следующие переменные:</p>
  <ul>
    <li><code>USERNAME_DB</code></li>
    <li><code>PASSWORD_DB</code></li>
    <li><code>SECRET_KEY</code></li>
    <li><code>ENCRYPTION_SECRET</code></li>
  </ul>
  <p>
    Пример значений есть в файле <code>pass.env</code>.
  </p>
</div>

<div>
  <h2>Инфраструктура БД</h2>
  <p>
    В репозитории есть <code>docker-compose.yml</code> для запуска PostgreSQL:
  </p>
  <ul>
    <li><code>POSTGRES_DB=bank_db</code></li>
    <li><code>POSTGRES_USER=pavel</code></li>
    <li><code>POSTGRES_PASSWORD=admin</code></li>
    <li><code>host port: 5433</code> → <code>container port: 5432</code></li>
  </ul>
  <p>
    При старте приложения миграции применяются автоматически через <code>Liquibase</code>.
  </p>
</div>

<hr />

<div>
  <h2>Запуск приложения</h2>

  <h3>1) Запустить PostgreSQL в Docker</h3>
  <pre><code>docker compose up -d</code></pre>

  <h3>2) Установить переменные окружения</h3>
  <p><span>PowerShell (Windows):</span></p>
  <pre><code>$env:USERNAME_DB="pavel"
$env:PASSWORD_DB="admin"
$env:SECRET_KEY="MyVeryLongAndSecureSecretKeyForJWTTokenSigningThatIsAtLeast64CharactersLongToMeetHS512Requirements123456789"
$env:ENCRYPTION_SECRET="your-strong-secret-key-here-change-in-production"</code></pre>

  <p><span>Bash (Linux/macOS):</span></p>
  <pre><code>export USERNAME_DB="pavel"
export PASSWORD_DB="admin"
export SECRET_KEY="MyVeryLongAndSecureSecretKeyForJWTTokenSigningThatIsAtLeast64CharactersLongToMeetHS512Requirements123456789"
export ENCRYPTION_SECRET="your-strong-secret-key-here-change-in-production"</code></pre>

  <h3>3) Запустить приложение</h3>
  <p><span>Windows:</span></p>
  <pre><code>.\mvnw.cmd spring-boot:run</code></pre>

  <p><span>Linux/macOS:</span></p>
  <pre><code>./mvnw spring-boot:run</code></pre>

  <h3>4) Проверить доступность</h3>
  <ul>
    <li>API: <a href="http://localhost:8081/api/v1">http://localhost:8081/api/v1</a></li>
    <li>Swagger UI: <a href="http://localhost:8081/swagger-ui.html">http://localhost:8081/swagger-ui.html</a></li>
  </ul>
</div>
