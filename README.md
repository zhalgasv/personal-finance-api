# Personal Finance API

REST API for personal finance management built with Spring Boot 3.

## Tech Stack

- Java 17
- Spring Boot 3.4.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Docker Compose
- OpenAPI / Swagger UI
- JUnit 5 + Mockito

## Features

- [x] User registration and login
- [x] JWT authentication
- [x] Protected transaction endpoints
- [x] Transaction CRUD
- [x] Transaction filtering by type and date range
- [x] Balance summary
- [x] Validation and global exception handling
- [x] Swagger UI documentation
- [x] Service tests
- [ ] Controller tests
- [ ] Category management
- [ ] Budget tracking
- [ ] Pagination


## Getting Started

Start PostgreSQL:

```bash
docker compose up -d
```

Run the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

The API runs on:

```text
http://127.0.0.1:8081
```

Swagger UI:

```text
http://127.0.0.1:8081/swagger-ui.html
```

OpenAPI JSON:

```text
http://127.0.0.1:8081/v3/api-docs
```

## Authentication

Register:

```bash
curl -X POST http://127.0.0.1:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"adil","password":"123456","email":"adil@test.com"}'
```

Login:

```bash
curl -X POST http://127.0.0.1:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"adil","password":"123456"}'
```

The login response returns a JWT token:

```json
{
  "token": "..."
}
```

Use the token for protected endpoints:

```bash
Authorization: Bearer <token>
```

## Transaction API

Create transaction:

```bash
curl -X POST http://127.0.0.1:8081/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"amount":1500,"description":"Salary","type":"INCOME","date":"2026-06-10"}'
```

Get all transactions:

```bash
curl http://127.0.0.1:8081/api/transactions \
  -H "Authorization: Bearer <token>"
```

Filter by type:

```bash
curl "http://127.0.0.1:8081/api/transactions?type=EXPENSE" \
  -H "Authorization: Bearer <token>"
```

Filter by date range:

```bash
curl "http://127.0.0.1:8081/api/transactions?from=2026-06-01&to=2026-06-10" \
  -H "Authorization: Bearer <token>"
```

Filter by type and date range:

```bash
curl "http://127.0.0.1:8081/api/transactions?type=INCOME&from=2026-06-01&to=2026-06-10" \
  -H "Authorization: Bearer <token>"
```

Get transaction by id:

```bash
curl http://127.0.0.1:8081/api/transactions/1 \
  -H "Authorization: Bearer <token>"
```

Update transaction:

```bash
curl -X PUT http://127.0.0.1:8081/api/transactions/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"amount":1200,"description":"Updated salary","type":"INCOME","date":"2026-06-10"}'
```

Delete transaction:

```bash
curl -X DELETE http://127.0.0.1:8081/api/transactions/1 \
  -H "Authorization: Bearer <token>"
```

Get balance:

```bash
curl http://127.0.0.1:8081/api/transactions/balance \
  -H "Authorization: Bearer <token>"
```

Example balance response:

```json
{
  "income": 5000,
  "expense": 1200,
  "balance": 3800
}
```

## Tests

Run all tests:

```bash
./mvnw test
```

## Notes

- `INCOME` means money received.
- `EXPENSE` means money spent.
- Transaction endpoints return only the authenticated user's data.
