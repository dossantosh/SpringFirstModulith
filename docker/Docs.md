Docker run guide (focus: Flyway)

This folder contains the Docker Compose setup used to run the app (and its database) locally or from prebuilt GitHub Container Registry (GHCR) images.

## 0) Environment variables

Use the env file that matches how you run the app:

- Root `.env`: local Spring Boot runs outside Docker
- `docker/.env`: Docker Compose runs

Examples:
- `DB_NAME`, `DB_HIST_NAME`, `DB_USER`, `DB_PASSWORD`
- `SERVER_PORT`, `DB_EXPOSED_PORT`, `FRONTEND_PORT`

Docker Compose loads `docker/.env` automatically when you run commands from this folder.

## 1) Prerequisites

- Docker + Docker Compose v2
- Ports available on your machine:
  - 5432 (PostgreSQL)
  - 7070 (Backend)
  - 4200 (Frontend)

## 2) PostgreSQL version requirement

The database image is pinned to **PostgreSQL 17** in `docker-compose.yml`.

Why: the first Flyway migration (`db/common/V1__schema_common.sql.sql`) includes:

  SET transaction_timeout = 0;

`transaction_timeout` is only supported in **PostgreSQL 17+**. If you run Postgres 16 or below, Flyway will fail on V1 and the application will not start.

## 3) Run using GHCR images (fastest)

From this folder:

  docker compose pull
  docker compose up -d

Then open:
- Frontend: http://localhost:4200
- Backend:  http://localhost:7070

## 4) Run locally (build backend/frontend from source)

From this folder:

  docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build

## 5) How Flyway behaves in Docker

- Docker creates both physical databases first via `01-create-databases.sh`.
- The backend then runs Flyway against both datasources.
- Flyway runs automatically when the backend starts.
- Migrations live under:
  - `src/main/resources/db/common/` (shared schema + seed)
  - (and any other module folders configured by `FlywayMultiDbConfig`)
- **Important:** Postgres data is stored in a named volume (`db_data`).
  - If the volume already exists, Postgres will reuse the same database.
  - Flyway will NOT re-run already applied migrations (it tracks them in `flyway_schema_history`).

This explains the common situation:
"It worked once, then my seed user didn’t change" → the seed migration already ran earlier.

## 6) Common Flyway/Docker workflows

### A) Start fresh (re-run all migrations, re-seed data)

⚠️ This deletes the database volume.

  docker compose down -v
  docker compose up -d

### B) You changed a seed/user/password but it doesn’t update

Don’t edit an old migration that already ran (e.g., V3). Instead:

1) Create a NEW migration (e.g., `V4__ensure_default_user.sql`).
2) Make it idempotent (insert only if missing).
3) Rebuild/restart backend.

### C) Check migration history

  docker compose exec db psql -U postgres -d SpringFirstModulithDB \
    -c "select installed_rank, version, description, success from flyway_schema_history order by installed_rank;"

### D) Check Postgres version in the container

  docker compose exec db psql -U postgres -c "select version();"

## 7) Stop containers

- Stop (keep DB volume):

  docker compose down

- Stop and delete DB volume (hard reset):

  docker compose down -v
