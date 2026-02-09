-- =========================
-- Seed data (COMMON)
-- =========================

-- ---- Roles ----
INSERT INTO roles (name)
VALUES
    ('ADMIN'),
    ('USER')
ON CONFLICT (name) DO NOTHING;


-- ---- Modules ----
INSERT INTO modules (name)
VALUES
    ('Users'),
    ('Perfumes')
ON CONFLICT (name) DO NOTHING;


-- ---- Submodules ----
-- IMPORTANT: resolve module IDs dynamically (do NOT hardcode 1,2)
INSERT INTO submodules (name, id_module)
SELECT 'ReadUsers', id_module FROM modules WHERE name = 'Users'
UNION ALL
SELECT 'WriteUsers', id_module FROM modules WHERE name = 'Users'
UNION ALL
SELECT 'ReadPerfumes', id_module FROM modules WHERE name = 'Perfumes'
UNION ALL
SELECT 'WritePerfumes', id_module FROM modules WHERE name = 'Perfumes'
ON CONFLICT (name, id_module) DO NOTHING;


-- ---- Users (fixed IDs for base users) ----
INSERT INTO users (id_user, email, enabled, is_admin, password, username)
VALUES
    (1, 'sebastiandossantosh@gmail.com', true, true,
     '$2b$10$w.f36eR/BwE8fVuSki1bJuQfbHcwcdqUJcqWKMlntMg2c1rt2rRpO', 'dossantosh'),
    (2, 'sebastiandossantosherrera@gmail.com', true, false,
     '$2b$10$w.f36eR/BwE8fVuSki1bJuQfbHcwcdqUJcqWKMlntMg2c1rt2rRpO', 'sevas')
ON CONFLICT (id_user) DO NOTHING;


-- ---- User ↔ Role mapping ----
INSERT INTO users_roles (id_user, id_role)
SELECT 1, id_role FROM roles WHERE name = 'ADMIN'
UNION ALL
SELECT 2, id_role FROM roles WHERE name = 'USER'
ON CONFLICT (id_user, id_role) DO NOTHING;


-- ---- User ↔ Module mapping ----
INSERT INTO users_modules (id_user, id_module)
SELECT 1, id_module FROM modules
UNION ALL
SELECT 2, id_module FROM modules WHERE name = 'Users'
ON CONFLICT (id_user, id_module) DO NOTHING;


-- ---- User ↔ Submodule mapping ----
INSERT INTO users_submodules (id_user, id_submodule)
SELECT 1, id_submodule FROM submodules
UNION ALL
SELECT 2, id_submodule FROM submodules WHERE name = 'ReadUsers'
ON CONFLICT (id_user, id_submodule) DO NOTHING;


-- ---- Keep users sequence in sync ----
SELECT setval(
    pg_get_serial_sequence('users', 'id_user'),
    COALESCE((SELECT MAX(id_user) FROM users), 0)
);


-- ---- Bulk test users (DEV SEED) ----
INSERT INTO users (email, enabled, is_admin, password, username)
SELECT
    'user' || gs || '@example.com',
    true,
    false,
    '$2a$10$FRYWPbBpdG3Snryo71URZuTk69hj7sn.Za2Z7Agc7xLbxBcExiAaq',
    'user_' || gs
FROM generate_series(10, 10010) gs
ON CONFLICT (username) DO NOTHING;
