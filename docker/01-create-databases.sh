#!/bin/sh
set -eu

create_db_if_missing() {
  db_name="$1"

  if [ -z "$db_name" ]; then
    echo "Database name is required" >&2
    exit 1
  fi

  if psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres -tAc \
    "SELECT 1 FROM pg_database WHERE datname = '$db_name'" | grep -q 1; then
    echo "Database '$db_name' already exists"
  else
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres \
      -c "CREATE DATABASE \"$db_name\""
    echo "Created database '$db_name'"
  fi
}

create_db_if_missing "$DB_NAME"
create_db_if_missing "$DB_HIST_NAME"
