-- Roles
INSERT INTO roles (name) VALUES
('ADMIN'),
('USER')
ON CONFLICT (name) DO NOTHING;

-- Módulos
INSERT INTO modules (name) VALUES 
('Users'), 
('Perfumes') 
ON CONFLICT (name) DO NOTHING;

-- Submódulos
INSERT INTO submodules (name, id_module) VALUES
('ReadUsers', 1),
('WriteUsers', 1),
('ReadPerfumes', 2),
('WritePerfumes', 2)
ON CONFLICT (name, id_module) DO NOTHING;

-- Users
INSERT INTO users (id_user, email, enabled, is_admin, password, username) VALUES
(1, 'sebastiandossantosh@gmail.com', true, true,  '$2b$10$w.f36eR/BwE8fVuSki1bJuQfbHcwcdqUJcqWKMlntMg2c1rt2rRpO', ''),
(2, 'sebastiandossantosherrera@gmail.com', true, false, '$2b$10$w.f36eR/BwE8fVuSki1bJuQfbHcwcdqUJcqWKMlntMg2c1rt2rRpO', 'sevas')
ON CONFLICT (id_user) DO NOTHING;

 INSERT INTO users_roles (id_user, id_role) VALUES
(1, 1),
(2, 2)
ON CONFLICT (id_user, id_role) DO NOTHING;

 INSERT INTO users_modules (id_user, id_module) VALUES
(1, 1),
(1, 2),
(2, 1)
ON CONFLICT (id_user, id_module) DO NOTHING;

INSERT INTO users_submodules (id_user, id_submodule) VALUES
(1,1),
(1, 2),
(1, 3),
(1, 4),
(2, 1)
ON CONFLICT (id_user, id_submodule) DO NOTHING;

-- Keep users id sequence in sync with existing rows
SELECT setval(
    pg_get_serial_sequence('users', 'id_user'),
    COALESCE((SELECT MAX(id_user) FROM users), 0)
);

-- Bulk users (test data)
INSERT INTO users (email, enabled, is_admin, password, username)
SELECT
  'user' || gs || '@example.com',
  true,
  false,
  '$2a$10$FRYWPbBpdG3Snryo71URZuTk69hj7sn.Za2Z7Agc7xLbxBcExiAaq',
  'user_' || gs
FROM generate_series(10, 10010) gs
ON CONFLICT (username) DO NOTHING;