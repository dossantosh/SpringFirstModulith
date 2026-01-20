-- Roles
INSERT INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Módulos
INSERT INTO modules (name, image) VALUES 
('All', 'image'), 
('Users', 'image'), 
('Perfumes', 'image') 
ON CONFLICT (name) DO NOTHING;
 
-- Submódulos
INSERT INTO submodules (name, id_module) VALUES
('ReadAll', 1),
('WriteAll', 1),
('ReadUsers', 2),
('WriteUsers', 2),
('ReadPerfumes', 3),
('WritePerfumes', 3)
ON CONFLICT (name, id_module) DO NOTHING;

-- Users
INSERT INTO users (id_user, email, enabled, is_admin, password, username) VALUES
(1, 'sebastiandossantosh@gmail.com', true, true,  '$2a$10$fRYWPbBpdG3Snryo71URzuTk69hj7sn.Za2Z7Agc7xLbxBcExiAaq', 'dossantosh'),
(2, 'sebastiandossantosherrera@gmail.com', true, false, '$2a$10$fRYWPbBpdG3Snryo71URzuTk69hj7sn.Za2Z7Agc7xLbxBcExiAaq', 'sevas')
ON CONFLICT (username) DO NOTHING;

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