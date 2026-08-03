# Task Management Application

A full-stack task management system built as part of a software internship mini-project.
Users can register, log in, and manage their own personal tasks — each task has a status
and priority, and tasks can be filtered by either. The backend is a Spring Boot REST API
secured with JWT; the frontend is an Angular single-page app.

---

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.3
- Spring Security (JWT-based auth)
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- Cucumber (BDD) + RestAssured

**Frontend**
- Angular (standalone components)
- Angular Material
- Reactive Forms

**DevOps**
- Docker + Docker Compose
- GitHub Actions (CI: build + test)

---

## Project Structure

```
task-manager-app/
├── backend/          # Spring Boot REST API
├── frontend/          # Angular app
├── docs/
│   └── erd.png         # Database schema diagram
└── .github/
    └── workflows/       # CI pipeline
```

---

## Entity-Relationship Diagram

<img width="412" height="852" alt="Screenshot 2026-08-03 235817" src="https://github.com/user-attachments/assets/a053bbbd-30ef-46c9-9533-eac313d5effd" />


Two tables: **users** and **tasks**, in a one-to-many relationship — every task belongs to
exactly one user via the `owner_id` foreign key. `status` and `priority` are stored as
strings but are backed by enums (`TaskStatus`, `TaskPriority`) at the application level, so
invalid values can't be persisted. `created_at` / `updated_at` are populated automatically
by JPA auditing.

---

## Getting Started

### Prerequisites
- Java 21 (JDK)
- Node.js + npm
- Angular CLI (`npm install -g @angular/cli`)
- PostgreSQL (running locally), **or** Docker Desktop if using the containerized setup below

### Option A — Run everything with Docker Compose (recommended)

From the repository root:

```bash
docker compose up --build
```

This starts both the PostgreSQL database and the backend API. The backend will be
available at `http://localhost:8080`. Then run the frontend separately (see below) —
Docker Compose currently covers backend + database only.


### Running the tests

```bash
cd backend
mvn test
```

This runs the full Cucumber BDD suite (`src/test/resources/features/*.feature`) against
an in-memory H2 database, using RestAssured to make real HTTP calls to a locally started
instance of the app (`webEnvironment = RANDOM_PORT`) — no external database or manual setup
required.

---


## Architecture

The backend follows a standard layered architecture:

```
Controller → Service → Repository → Database
```

- **Controllers** handle HTTP concerns only (request/response mapping, status codes)
- **Services** hold business logic (e.g. "a task can only be updated by its owner")
- **Repositories** (Spring Data JPA) handle persistence
- **DTOs** are used at the controller boundary so the API's shape is never directly tied
  to the database schema

---

## Design Patterns & OOP Principles

This project applies the following patterns and principles deliberately, not
incidentally:

| Pattern / Principle | Where | Why |
|---|---|---|
| **DTO (Data Transfer Object)** | `dto/` package (`TaskRequest`, `TaskResponse`, `AuthResponse`, etc.) | Controllers never return JPA entities directly — the API contract stays independent of the database schema. |
| **Repository pattern** | `UserRepository`, `TaskRepository` | Data access logic is isolated behind an interface (Spring Data JPA implements it at runtime). |
| **Layered architecture** | Controller → Service → Repository | Each layer has one responsibility and only talks to the layer directly below it. |
| **Dependency Injection** | Constructor injection throughout (`@RequiredArgsConstructor` / explicit constructors) | Services and controllers receive their dependencies rather than constructing them — makes the code testable and swappable. |
| **Adapter pattern** | `CustomUserDetailsService` | Adapts the app's own `User` entity into Spring Security's `UserDetails` interface, so Spring Security never depends on the app's domain model directly. |
| **Chain of Responsibility** | `JwtAuthFilter` | Plugs into Spring Security's filter chain — each filter decides whether to act and passes the request along. |
| **Static Factory Method** | `TaskResponse.fromEntity(task)` | Centralizes entity → DTO mapping in one place instead of repeating it across controllers. |
| **Inheritance** | `BaseEntity` → `User`, `Task` | Shared `id` and audit timestamp fields live in one base class instead of being duplicated. |
| **Encapsulation** | Entity fields are private; password hashing only ever happens in the service layer, never in the entity | Keeps responsibility for sensitive logic in one well-defined place. |
| **Abstraction** | Code depends on interfaces (`UserDetailsService`, `PasswordEncoder`, `JpaRepository`) rather than concrete implementations | Standard Spring practice — implementations can change without touching consumers. |
