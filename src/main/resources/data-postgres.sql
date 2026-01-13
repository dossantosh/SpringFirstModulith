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
(1, 'sebastiandossantosh@gmail.com', true, true, '$2a$10$fRYWPbBpdG3Snryo71URzuTk69hj7sn.Za2Z7Agc7xLbxBcExiAaq', 'dossantosh')
ON CONFLICT (username) DO NOTHING;