# Virtual Card Project

Backend для системы выпуска виртуальных банковских карт.

## Use Case

1. Пользователь регистрируется через `user-service` и получает JWT.
2. Авторизованный пользователь отправляет заявку на выпуск карты в `card-service`.
3. `card-service` получает необходимые данные пользователя через REST-запрос к `user-service`.
4. Заявка проходит scoring.
5. После успешного выпуска карты `card-service` сохраняет событие в Outbox.
6. Событие публикуется в Kafka.
7. `notification-service` обрабатывает событие и отправляет уведомление.
8. `pdf-document-service` генерирует PDF с информацией о выпущенной карте.

## Архитектура

Проект состоит из четырёх микросервисов:

* **user-service** — регистрация и аутентификация пользователей, генерация JWT-токенов и хранение пользовательских данных.
* **card-service** — обработка заявок на выпуск карт, получение данных о пользователе, скоринг и управление банковскими картами.
* **notification-service** — обработка событий и отправка уведомлений.
* **pdf-document-service** — генерация PDF-документов.

`card-service` реализует синхронное взаимодействие с `user-service` через REST API.

Для асинхронного взаимодействия между `card-service`, `notification-service` и `pdf-document-service` используется Apache Kafka.
Для надёжной публикации событий используется паттерн Transactional Outbox:
изменение статуса заявки в базе данных и запись события в Outbox выполняются в одной транзакции, 
после чего отдельный процесс публикует событие в Kafka.

```text
                    ┌────────────────┐
                    │  user-service  │
                    │                │
      ┌───────────► │ Auth + JWT     │
      │             └───────▲────────┘
      │                     │ REST
      │                     │
┌───────────┐        ┌──────┴───────┐
│  Client   │ ──────►│ card-service │
└───────────┘        └──────┬───────┘
                            │
                          Kafka
                            │
                ┌───────────┴───────────┐
                ▼                       ▼
       ┌─────────────────┐    ┌────────────────────┐
       │ notification-   │    │ pdf-document-      │
       │ service         │    │ service            │
       └─────────────────┘    └────────────────────┘
```

## Стек

* Java 21
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA / Hibernate
* PostgreSQL
* Flyway
* Apache Kafka
* JUnit / Mockito / Testcontainers
* Maven
* Docker / Docker Compose

## Структура проекта

```text
virtual-card-project/
├── card-service/
├── user-service/
├── notification-service/
├── pdf-document-service/
└── docker-compose.yml
```

Каждый сервис является отдельным Maven-проектом и имеет собственный `Dockerfile`.

## Запуск

### Зависимости

- JDK 21
- Docker
- Docker Compose

### Команда запуска

```bash
docker compose up --build
```

После запуска:

* `card-service` — `localhost:8080`
* `user-service` — `localhost:8081`
* Kafka — `localhost:9092`
* PostgreSQL для `card-service` — `localhost:5432`
* PostgreSQL для `user-service` — `localhost:5433`

## Тесты

Запуск в Linux / macOS:

```bash
./mvnw clean verify
```

Windows:

```bash
./mvnw.cmd clean verify
```
