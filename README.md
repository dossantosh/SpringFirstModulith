# SpringFirstModulith

## 📖 Table of Contents

- [📌 Project Overview](#-project-overview)
- [✨ Features](#-features)
  - [👥 User & Security](#-user--security)
  - [🌐 Web & API](#-web--api)
  - [📦 Architecture](#-architecture)
  - [📊 Reports & Exports](#-reports--exports)
  - [⚙️ Dev & Ops](#️-dev--ops)
- [🗂️ Project Structure](#️-project-structure)
- [🛠️ Tech Stack](#️-tech-stack)
- [🧾 Environment Configuration (.env)](#-environment-configuration-env)
- [⚙️ Application Properties](#️-application-properties)
- [🚀 Getting Started (Docker-first)](#-getting-started-docker-first)
- [🐳 Docker Compose (DB + App)](#-docker-compose-db--app)
- [🔒 Security Notes](#-security-notes)
- [🧪 Testing with Docker](#-testing-with-docker)
- [👤 Author](#-author)
- [📬 Contact](#-contact)
- [🙏 Acknowledgements](#-acknowledgements)

---

## 📌 Project Overview

A **Spring Modulith–based web application** focused on demonstrating a **modular monolith architecture** using Spring Boot Modulith.

The project showcases user authentication, security enforcement, and domain-oriented modules such as users and perfumes. It demonstrates how to structure a growing system with **clear module boundaries**, controlled dependencies, and architectural enforcement—while remaining a single deployable application.

Designed as a learning and reference project, it emphasizes **modularity, security, and maintainability** without prematurely moving to microservices.

---

## ✨ Features

### 👥 User & Security

- User authentication and session handling
- Centralized security module
- Authentication controllers isolated in a dedicated module
- Session-aware request filtering
- Clear separation between authentication, security, and user domains
- Module boundaries enforced at package level

### 🌐 Web & API

- RESTful endpoints for authentication and domain access
- DTO-based API design
- Filter-based request context handling
- Separation between API, domain, and infrastructure layers

### 📦 Architecture

**Auth Module:** Authentication entrypoints and login flows

- Handles login, logout, and authentication-related controllers
- Does _not_ contain business or security policy logic
- Prevents authentication concerns from leaking into domain modules

**Users Module:** Core user domain and business rules

- Encapsulates user entities, use cases, and domain behavior
- Exposes functionality through **ports**
- Uses adapters for persistence and external integrations
- Remains independent from authentication and security infrastructure

**Perfumes Module:** Product and catalog domain

- Owns product-related business logic
- Completely decoupled from identity, authentication, and security concerns
- Can evolve independently without impacting other domains

**Security Module:** Technical security enforcement

- Centralizes Spring Security configuration
- Handles filters, session handling, and authorization rules
- Contains _technical_ security concerns only
- Does not encode business meaning or domain logic

**Core Module:** contains **domain-agnostic technical infrastructure**, such as:

- Datasource routing
- Error handling primitives
- Pagination and shared technical utilities

This:

- Is reusable across domains
- Contains no business rules
- Exists to support, not control, domain modules

**Boundaries Enforcement:**

- **Module boundaries are explicitly declared** using `package-info.java`
- Dependencies between modules are intentional and documented
- **Spring Modulith architecture tests** prevent:
  - Illegal dependencies
  - Accidental cross-module coupling
  - Infrastructure leaking into domain logic

This ensures the architecture stays clean as the system grows.

---

### 📊 Reports & Exports

- Not implemented yet

### ⚙️ Dev & Ops

- `.env` support for local configuration
- SQL initialization configuration
- Session-based datasource routing
- Clean separation of runtime and test configuration

---

## 🗂️ Project Structure

```
com.dossantosh.springfirstmodulith
├── auth
│   ├── controllers
│   └── package-info.java
│
├── core
│   ├── datasource
│   ├── errors
│   ├── page
│   └── package-info.java
│
├── perfumes
│   └── package-info.java
│
├── security
│   ├── login
│   ├── package-info.java
│   ├── SecurityConfig.java
│   └── SessionCookieConfig.java
│
├── users
│   ├── api
│   │   └── controllers
│   │
│   ├── ports
│   │
│   ├── application
│   │   ├── dtos
│   │   └── services
│   │
│   ├── domain
│   │   └── Jpa Models
│   │
│   ├── infrastructure
│   │   ├── adapters
│   │   ├── projections
│   │   └── repos
│   │
│   └── package-info.java
│
└── SpringfirstmodulithApplication.java
```

### 🗂️ Database Resources

```
src/main/resources/db
├── common
│   ├── V1__schema_common.sql.sql
│   ├── V2__schema_security.sql
│   └── V3__seed.sql
```

- **Flyway is the single source of truth** for schema & seed data
- No Hibernate schema generation is relied upon for production runs

---

# 🛠️ Tech Stack

Below is a detailed breakdown of the technologies used and their purpose within the system.

| Stack                             | Description                                                   |
| --------------------------------- | ------------------------------------------------------------- |
| **Java 25**                       | Language level configured for compilation and runtime         |
| **Spring Boot 4.0.2**             | Core application framework and dependency management          |
| **Spring Web**                    | REST endpoints and MVC support                                |
| **Spring Security**               | Authentication and authorization infrastructure               |
| **Spring Validation**             | Input and request validation                                  |
| **Spring Data JPA**               | ORM layer using Hibernate                                     |
| **Spring Boot Actuator**          | Monitoring and management endpoints                           |
| **Spring Modulith 2.0.1**         | Modular monolith architecture with enforced module boundaries |
| **Spring Modulith JPA**           | JPA integration for Modulith modules                          |
| **Spring Modulith Actuator**      | Runtime visibility of module structure                        |
| **Spring Modulith Observability** | Tracing and observability support                             |
| **PostgreSQL**                    | Primary relational database                                   |
| **Spring JDBC**                   | Low-level JDBC support                                        |
| **Spring Session JDBC**           | Persistent session storage backed by the database             |
| **Jackson Databind**              | JSON serialization and deserialization                        |
| **Lombok**                        | Boilerplate code reduction                                    |
| **Spring Boot DevTools**          | Development-time hot reload support                           |
| **Spring Boot Docker Compose**    | Local development Docker Compose integration                  |
| **JUnit 5**                       | Unit and integration testing framework                        |
| **Mockito**                       | Mocking framework for tests                                   |
| **Spring Security Test**          | Security-focused testing utilities                            |
| **Spring Modulith Test**          | Architectural and module dependency tests                     |
| **Testcontainers (PostgreSQL)**   | Integration tests using real PostgreSQL containers            |
| **H2**                            | In-memory database for lightweight testing                    |

---

## 🧾 Environment Configuration (.env)

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=SpringFirstModulithDB

DB_HIST_NAME=SpringFirstModulithDBHistoric

SERVER_PORT=9090

DB_USER=user
DB_PASSWORD=secret

MANAGEMENT_PORT=9090
```

---

## ⚙️ Application Properties

This project relies on **explicit, environment-driven configuration** to support
multi-datasource routing, session persistence, and observability.
Configuration is defined in `application.properties` and can be overridden via a `.env` file.

---

### 🌱 Configuration Loading

```properties
spring.application.name=springfirstmodulith
spring.config.import=optional:file:.env[.properties]
```

- Loads environment variables from a local `.env` file when present
- Keeps secrets and environment-specific values out of version control

---

### 🗄️ Datasource Configuration (Routing)

The application uses **three logical datasources**, each backed by PostgreSQL:

- **prod** → main transactional data
- **historic** → audit / historic / read-heavy data
- **session** → Spring Session JDBC storage

#### Main (Transactional) Datasource

```properties
app.datasource.prod.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
app.datasource.prod.username=${DB_USER}
app.datasource.prod.password=${DB_PASSWORD}
app.datasource.prod.hikari.minimum-idle=5
app.datasource.prod.hikari.maximum-pool-size=100
app.datasource.prod.hikari.idle-timeout=50000
```

#### Historic Datasource

```properties
app.datasource.historic.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_HIST_NAME}
app.datasource.historic.username=${DB_USER}
app.datasource.historic.password=${DB_PASSWORD}
app.datasource.historic.hikari.minimum-idle=1
app.datasource.historic.hikari.maximum-pool-size=20
app.datasource.historic.hikari.idle-timeout=50000
```

- Isolated from transactional load
- Intended for audit, history, and reporting

#### Session Datasource

```properties
app.datasource.session.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
app.datasource.session.username=${DB_USER}
app.datasource.session.password=${DB_PASSWORD}
app.datasource.session.hikari.minimum-idle=1
app.datasource.session.hikari.maximum-pool-size=20
app.datasource.session.hikari.idle-timeout=50000
```

- Dedicated connection pool for Spring Session
- Prevents session traffic from starving domain queries

---

### 🧬 JPA / Hibernate

```properties
spring.jpa.defer-datasource-initialization=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

- **Hibernate validates schema only**
- Flyway migrations must already be applied
- Prevents accidental schema drift

Batch & performance tuning:

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=100
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
spring.jpa.properties.hibernate.generate_statistics=true
```

---

### 🧪 SQL Initialization & Seed Data

```properties
spring.sql.init.mode=never
```

- No `schema.sql` or `data.sql`
- All schema + seed handled by Flyway

---

### 🛫 Flyway

```properties
spring.flyway.enabled=false
```

- Flyway is **bootstrapped manually**
- Separate Flyway instances per datasource
- Full control over migration order and ownership

---

### 🧾 Spring Session (JDBC)

```properties
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always
spring.session.jdbc.platform=postgresql
server.servlet.session.timeout=30m
spring.session.jdbc.cleanup-cron=0 */5 * * * *
```

- Sessions are persisted in PostgreSQL
- Expired sessions are cleaned every 5 minutes

---

### 🔐 Session Cookie Hardening

```properties
app.security.session-cookie.same-site=Lax
app.security.session-cookie.secure=false
```

- Defaults are safe for local development
- Should be overridden to `SameSite=None` + `secure=true` in production

---

### 🌐 Server & Reverse Proxy Support

```properties
server.forward-headers-strategy=framework
server.port=${SERVER_PORT}
spring.web.error.whitelabel.enabled=false
```

- Correctly handles `X-Forwarded-*` headers when behind a proxy
- Disables Spring’s default error page

---

### 🐳 Docker Integration

```properties
spring.docker.compose.enabled=false
```

- Disabled explicitly to avoid unintended container startup
- Docker lifecycle is managed externally via Compose

---

### 📊 Actuator & OpenAPI

```properties
management.endpoints.web.exposure.include=health,info,metrics,env,beans,loggers,httpexchanges,auditevents,hikari
management.endpoint.health.show-details=always
management.endpoints.web.base-path=/actuator

springdoc.show-actuator=true
springdoc.use-management-port=true
springdoc.api-docs.path=/v3/api-docs
```

- Actuator endpoints exposed for monitoring
- OpenAPI includes actuator endpoints for observability

---

### 🧾 Logging Strategy

- SQL logging disabled by default
- Security and session noise reduced
- HikariCP logs tuned to avoid pool spam

```properties
logging.level.root=INFO
logging.level.org.hibernate.SQL=OFF
logging.level.org.springframework.security=INFO
logging.level.org.springframework.session=INFO
```

---

## 🚀 Getting Started (Docker-first)

### 1️⃣ Start PostgreSQL using Docker

The test database is already created in the docker-compose

---

### 2️⃣ Database Initialization

- Database schema is generated automatically via **Flyway**
- Spring Session JDBC tables are initialized on startup
- The **main DB** stores transactional data
- The **historic DB** stores audit / historic data only
- Seed scripts insert:
  - Roles, modules, submodules
  - Default users
  - Bulk test users

---

### 3️⃣ Run the Application (Locally)

Once PostgreSQL is running:

```sh
mvn clean install
mvn spring-boot:run
```

The application will start on:

- **Application**: `http://localhost:9090`
- **Swagger UI**: `http://localhost:9090/swagger-ui.html`
- **Actuator**: `http://localhost:9090/actuator`

---

### 4️⃣ Default Users (Development Only)

The application ships with **pre-seeded users** for development:

**Admin**

- Username: `dossantosh`
- Password: `password`
- Role: `ADMIN`
- Modules: Users, Perfumes

**Regular user**

- Username: `sevas`
- Password: `password`
- Role: `USER`

> ⚠️ Default credentials are for **development only** and must be disabled or changed in production.

---

## 🐳 Docker Compose (DB + App)

You can run the entire stack using Docker Compose.

### docker-compose.yml

```yaml
services:
  db:
    image: postgres:17
    environment:
      POSTGRES_DB: SpringFirstModulithDB
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: Sb202582
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data
      - ./01-create-historic-db.sql:/docker-entrypoint-initdb.d/01-create-historic-db.sql:ro

  backend:
    image: ghcr.io/dossantosh/springfirstmodulith:main
    #image: ghcr.io/dossantosh/springfirstmodulith:flywayImplementation
    environment:
      #SPRING_SESSION_JDBC_INITIALIZE_SCHEMA: never
      DB_HOST: db
      DB_PORT: 5432
      DB_NAME: SpringFirstModulithDB
      DB_HIST_NAME: SpringFirstModulithDBHistoric
      DB_USER: postgres
      DB_PASSWORD: Sb202582
      SERVER_PORT: 9090
    ports:
      - "9090:9090"
    depends_on:
      - db

  frontend:
    image: ghcr.io/dossantosh/angularmodulith:main
    #image: ghcr.io/dossantosh/angularmodulith:Database-Runtime-Routing
    ports:
      - "4200:80"
    depends_on:
      - backend

volumes:
  db_data:
```

### docker-compose.dev.yml

```yaml
services:
  backend:
    build:
      context: ../../SpringFirstModulith
    image: springfirstmodulith:dev

  frontend:
    build:
      context: ../../AngularModulith
    image: angularmodulith:dev
```

### Init script for historic DB

**docker/initdb/01-create-historic-db.sql**

```sql
-- Create historic DB (only if it doesn't exist)
SELECT 'CREATE DATABASE "SpringFirstModulithDBHistoric"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'SpringFirstModulithDBHistoric')\gexec
```

Run everything:

```sh
docker compose up --build
```

Run in dev

```sh
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
```

Stop containers:

```sh
docker compose down
```

Reset volumes:

```sh
docker compose down -v
```

---

## 🔒 Security Notes

- Session-based authentication (no JWT)
- Cookies configured as `HttpOnly`, `Secure`, `SameSite`
- CSRF enabled for browser endpoints
- Authorization enforced via roles, modules, and submodules

---

## 🧪 Testing with Docker

```sh
mvn test
```

- Requires Docker running locally
- Uses **Testcontainers** with PostgreSQL
- Includes **Spring Modulith architecture tests** to prevent illegal dependencies

---

## 👤 Author

Sebastián Dos Santos

---

## 📬 Contact

- GitHub: https://github.com/dossantosh
- LinkedIn: https://www.linkedin.com/in/dossantosh/
- Email: sebastiandossantosh@gmail.com

---

## 🙏 Acknowledgements

- Spring Modulith team and documentation
- Clean Architecture & Modular Monolith community
