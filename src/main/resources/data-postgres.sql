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
ON CONFLICT (name, id_module) DO NOTHING;  -- if (name, id_module) is unique