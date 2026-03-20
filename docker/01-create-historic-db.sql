-- Create historic DB (only if it doesn't exist)
SELECT 'CREATE DATABASE "SpringFirstModulithDBHistoric"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'SpringFirstModulithDBHistoric')\gexec