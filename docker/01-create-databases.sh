#!/bin/sh
set -eu

require_env() {
  var_name="$1"
  var_value="$2"

  if [ -z "$var_value" ]; then
    echo "Environment variable '$var_name' is required" >&2
    exit 1
  fi
}

create_db_if_missing() {
  db_name="$1"

  if psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres -tAc \
    "SELECT 1 FROM pg_database WHERE datname = '$db_name'" | grep -q 1; then
    echo "Database '$db_name' already exists"
  else
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres \
      -c "CREATE DATABASE \"$db_name\""
    echo "Created database '$db_name'"
  fi
}

main_db_name="${DB_NAME-}"
historic_db_name="${DB_HIST_NAME-}"
postgres_user="${POSTGRES_USER-}"

require_env "DB_NAME" "$main_db_name"
require_env "DB_HIST_NAME" "$historic_db_name"
require_env "POSTGRES_USER" "$postgres_user"

create_db_if_missing "$main_db_name"
create_db_if_missing "$historic_db_name"
