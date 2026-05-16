# Expense Tracker API

A secure, production-structured REST API for tracking personal expenses, managing
spending budgets, and organising categories — built with Spring Boot 4 and Java 22.

> **Portfolio project** by [Thembela Tole](https://github.com/placeholder)

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running with Docker](#running-with-docker)
  - [Running Locally](#running-locally)
- [Environment Variables](#environment-variables)
- [Database Seed Data](#database-seed-data)
- [API Reference](#api-reference)
  - [Authentication](#authentication)
  - [Expenses](#expenses)
  - [Categories](#categories)
  - [Budgets](#budgets)
  - [User](#user)
  - [User Profile](#user-profile)
  - [Admin — User Management](#admin--user-management)
- [Enums](#enums)
- [Error Handling](#error-handling)
- [Security Model](#security-model)
- [Project Structure](#project-structure)

---

## Overview

The Expense Tracker API allows users to register an account, log expenses against
categories, and set budgets per category and time period. The API enforces strict
owner-level data isolation — users can only access their own data. Administrative
operations require the `ROLE_MANAGER` authority.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 22 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security 6 · JJWT 0.13.0 |
| Persistence | Spring Data JPA · PostgreSQL 17 |
| Validation | Jakarta Validation 3 |
| Documentation | Springdoc OpenAPI 3.0.3 (Swagger UI) |
| Containerisation | Docker · Docker Compose |
| Build | Maven |

---

## Features

- **JWT authentication** with short-lived access tokens and rotating refresh tokens
  stored as `HttpOnly; Secure` cookies
- **Session management** — logout from the current device or all devices at once
- **Owner-scoped resources** — every expense, category, and budget is isolated per user
- **Expense filtering** by predefined period (`PAST_WEEK`, `PAST_MONTH`, `LAST_3_MONTHS`)
  or a custom date range
- **Budget warnings** returned inline on expense creation when a category budget is
  exceeded
- **System categories** seeded on startup (Groceries, Utilities, Health, and more)
- **Admin endpoints** for enabling, disabling, and deleting user accounts
- **Interactive API docs** via Swagger UI at `/swagger-ui/index.html`

---

## Getting Started

### Prerequisites

- [Docker](https://www.docker.com/) and Docker Compose — for the containerised setup
- Java 22 and Maven — for running locally without Docker

### Running with Docker

**1. Clone the repository**
```bash
git clone https://github.com/placeholder/expense-tracker-api.git
cd expense-tracker-api
```

**2. Create your environment file**
```bash
cp .env.example .env
```
Edit `.env` and fill in all values. See [Environment Variables](#environment-variables) below.

**3. Start the services**
```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.
Swagger UI will be available at `http://localhost:8080/swagger-ui/index.html`.

**Stop the services**
```bash
docker compose down
```

**Stop and remove volumes (wipes the database)**
```bash
docker compose down -v
```

---

### Running Locally

**1. Start a PostgreSQL instance** on port `5433` with a database named `expense-tracker-api`.

**2. Create your environment file**
```bash
cp .env.example .env
```

**3. Set DB_HOST and DB_PORT in `.env`**
```
DB_HOST=localhost
DB_PORT=5433
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

---

## Environment Variables

Copy `.env.example` to `.env` and fill in the values. **Never commit `.env`.**

| Variable | Description | Example |
|---|---|---|
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Host-side database port | `5433` |
| `DB_NAME` | Database name | `expense-tracker-api` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | — |
| `JWT_SECRET_KEY` | Base64-encoded secret, minimum 32 bytes | — |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token TTL in milliseconds | `900000` (15 min) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token TTL in milliseconds | `604800000` (7 days) |
| `JWT_ISSUER` | JWT issuer claim | `Expense-Tracker-API` |
| `SPRINGDOC_API_DOCS_ENABLED` | Enable OpenAPI spec endpoint | `true` |
| `SPRINGDOC_SWAGGER_UI_ENABLED` | Enable Swagger UI | `true` |

> **Production:** Set both `SPRINGDOC_*` variables to `false`. Exposing the API spec
> in production gives attackers a complete map of your endpoints and schemas.

**Generating a secure JWT secret key:**
```bash
openssl rand -base64 64
```

---

## Database Seed Data

The application seeds two sets of data on startup via `spring.sql.init.mode: always`.

**Authorities** — the two roles the application recognises:

| Authority | Description |
|---|---|
| `ROLE_USER` | Standard user — access to own expenses, categories, budgets, and profile |
| `ROLE_MANAGER` | Administrator — can enable, disable, and delete any user account |

**System categories** — available to all users automatically:

| Name | Description |
|---|---|
| Groceries | Food and grocery shopping |
| Leisure | Entertainment and recreation |
| Electronics | Electronic devices and gadgets |
| Utilities | Bills, electricity, water, internet |
| Clothing | Clothes and accessories |
| Health | Medical expenses and healthcare |
| Others | Miscellaneous expenses |

Users can also create their own custom categories alongside these system ones.

---

## API Reference

The full interactive documentation is available at:
```
http://localhost:8080/swagger-ui/index.html
```

All authenticated endpoints require the `Authorization: Bearer <access_token>` header.

### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Register a new account |
| `POST` | `/api/v1/auth/login` | Public | Log in — returns access token; sets refresh token cookie |
| `POST` | `/api/v1/auth/refresh` | Cookie | Rotate the refresh token and get a new access token |
| `POST` | `/api/v1/auth/logout` | JWT | Invalidate the current session |
| `POST` | `/api/v1/auth/logoutAll` | JWT | Invalidate all sessions for the user |

### Expenses

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/users/{userId}/expenses` | Record a new expense |
| `GET` | `/api/v1/users/{userId}/expenses` | List expenses (supports filtering — see below) |
| `GET` | `/api/v1/users/{userId}/expenses/{expenseId}` | Get a single expense |
| `PUT` | `/api/v1/users/{userId}/expenses/{expenseId}` | Update an expense |
| `DELETE` | `/api/v1/users/{userId}/expenses/{expenseId}` | Delete an expense |

**Filter parameters for `GET /expenses`:**

| Parameter | Type | Description |
|---|---|---|
| `period` | `String` | `PAST_WEEK`, `PAST_MONTH`, `LAST_3_MONTHS`, or `CUSTOM` |
| `categoryId` | `UUID` | Filter by category |
| `startDate` | `LocalDate` | Start of custom range (`yyyy-MM-dd`) — must be paired with `endDate` |
| `endDate` | `LocalDate` | End of custom range (`yyyy-MM-dd`) — must be paired with `startDate` |

### Categories

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/users/{userId}/categories` | Create a category |
| `GET` | `/api/v1/users/{userId}/categories` | List all categories |
| `GET` | `/api/v1/users/{userId}/categories/{categoryId}` | Get a single category |
| `PUT` | `/api/v1/users/{userId}/categories/{categoryId}` | Update a category |
| `DELETE` | `/api/v1/users/{userId}/categories/{categoryId}` | Delete a category |

### Budgets

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/users/{userId}/budgets` | Create a budget |
| `GET` | `/api/v1/users/{userId}/budgets` | List all budgets |
| `GET` | `/api/v1/users/{userId}/budgets/{budgetId}` | Get a single budget |
| `PUT` | `/api/v1/users/{userId}/budgets/{budgetId}` | Update a budget |
| `DELETE` | `/api/v1/users/{userId}/budgets/{budgetId}` | Delete a budget |

### User

| Method | Endpoint | Description |
|---|---|---|
| `PUT` | `/api/v1/users/{userId}` | Update account email or password |

### User Profile

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/users/profile` | Create profile for the authenticated user |
| `GET` | `/api/v1/users/profile/{userId}` | Get profile |
| `PUT` | `/api/v1/users/profile/{userId}` | Update profile |

### Admin — User Management

Requires `ROLE_MANAGER` authority.

| Method | Endpoint | Description |
|---|---|---|
| `PATCH` | `/api/v1/admin/users/{userId}/disable` | Disable a user account |
| `PATCH` | `/api/v1/admin/users/{userId}/enable` | Enable a user account |
| `DELETE` | `/api/v1/admin/users/{userId}` | Permanently delete a user account |

---

## Enums

### BudgetPeriod

| Value | Description |
|---|---|
| `WEEKLY` | 7-day budget window |
| `MONTHLY` | Calendar month window |
| `QUARTERLY` | 3-month window |
| `YEARLY` | 12-month window |
| `CUSTOM` | Use `startDate` and `endDate` to define the window |

### CategoryType

| Value | Description |
|---|---|
| `SYSTEM` | Seeded by the application — available to all users |
| `USER` | Created by an individual user |

---

## Error Handling

All errors return a consistent JSON envelope:

```json
{
  "error": "Bad Request",
  "errorCode": 400,
  "message": "Validation failed for one or more fields",
  "path": "/api/v1/users/3fa85f64/expenses",
  "timestamp": "2025-05-10T08:15:30",
  "details": {
    "amount": "Amount must be greater than zero",
    "expenseDate": "Expense Date is required"
  }
}
```

The `details` field is present only on `400` validation errors.
All other error responses return `null` for `details`.

---

## Security Model

- Stateless — no server-side sessions; every request is authenticated via JWT
- Access tokens are short-lived (15 minutes by default) and sent in the `Authorization` header
- Refresh tokens are long-lived (7 days) and stored in an `HttpOnly; Secure; SameSite=Strict`
  cookie, scoped to `/api/v1/auth/refresh`
- Every resource endpoint enforces owner-level isolation via Spring Security method
  expressions — `authentication.principal.id.equals(#userId)`
- Unauthenticated requests return a structured `401` JSON response (no redirect)
- Forbidden requests return a structured `403` JSON response

---

## Project Structure

```
src/main/java/com/teetech/expensetrackerapi/
├── config/          # OpenAPI config
├── controller/      # REST controllers
├── dto/             # Request, response, and filter DTOs
├── entity/          # JPA entities
├── enums/           # BudgetPeriod, CategoryType, PredefinedCategory
├── exception/       # Exception classes and global handler
├── repository/      # Spring Data JPA repositories
├── security/
│   ├── config/      # SecurityFilterChain
│   ├── filter/      # JwtFilter
│   ├── handler/     # AuthenticationEntryPoint, AccessDeniedHandler
│   └── model/       # UserPrincipal
├── service/         # Business logic
└── util/            # JwtUtil, JwtProperties, SecurityConstants
└── validation/      # Vaidation logic
```

---

*Built by Thembela Tole — [GitHub](https://github.com/TeeThembela)*
