# Virtual Card Project

Backend для системы выпуска виртуальных банковских карт.

Проект состоит из нескольких Spring Boot микросервисов и запускается с помощью Docker Compose.

## Use Case

Пользователь регистрируется или проходит аутентификацию в `user-service` и получает JWT-токен.

С помощью полученного токена пользователь может отправить заявку на выпуск виртуальной карты в `card-service`.

`card-service` получает необходимые данные о пользователе из `user-service`, выполняет проверку и скоринг заявки, после чего принимает решение о выпуске карты.

При обработке заявки сервисы взаимодействуют через Kafka. События о результатах обработки могут использоваться для отправки уведомлений пользователю и генерации необходимых документов.

## Архитектура

Проект состоит из четырёх микросервисов:

* **user-service** — регистрация и аутентификация пользователей, генерация JWT-токенов и хранение пользовательских данных.
* **card-service** — обработка заявок на выпуск карт, получение данных о пользователе, скоринг и управление банковскими картами.
* **notification-service** — обработка событий и отправка уведомлений.
* **pdf-document-service** — генерация PDF-документов.

Для синхронного взаимодействия `card-service` использует REST API `user-service`.

Для асинхронного взаимодействия между сервисами используется Apache Kafka. События, публикуемые `card-service`, обрабатываются `notification-service` и `pdf-document-service`.

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
* Apache Kafka
* JUnit
* Mockito
* Testcontainers
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

Для запуска всех сервисов и необходимых зависимостей:

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

Для запуска тестов:

```bash
./mvnw test
```

В проекте используются JUnit, Mockito и Testcontainers.
