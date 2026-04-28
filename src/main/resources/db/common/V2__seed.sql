INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('USER') ON CONFLICT (name) DO NOTHING;

INSERT INTO modules (name)
VALUES ('USERS'),
       ('PERFUMES') ON CONFLICT (name) DO NOTHING;

INSERT INTO submodules (name, id_module)
SELECT 'SEARCH_USERS', id_module
FROM modules
WHERE name = 'USERS' ON CONFLICT (name, id_module) DO NOTHING;

INSERT INTO scopes (name, description)
VALUES ('users:read', 'Read users'),
       ('users:create', 'Create users'),
       ('users:update', 'Update users'),
       ('users:delete', 'Delete users'),
       ('perfumes:read', 'Read perfumes'),
       ('perfumes:create', 'Create perfumes'),
       ('perfumes:update', 'Update perfumes'),
       ('perfumes:delete', 'Delete perfumes'),
       ('role:read', 'Read roles'),
       ('role:assign', 'Assign roles to users'),
       ('scope:read', 'Read scopes'),
       ('scope:assign', 'Assign direct scopes to users') ON CONFLICT (name) DO NOTHING;

INSERT INTO role_scopes (id_role, id_scope)
SELECT r.id_role, s.id_scope
FROM roles r
JOIN scopes s ON s.name IN ('users:read', 'users:create', 'users:update', 'users:delete', 'perfumes:read',
                            'perfumes:create', 'perfumes:update', 'perfumes:delete', 'role:read',
                            'role:assign', 'scope:read', 'scope:assign')
WHERE r.name = 'ADMIN' ON CONFLICT (id_role, id_scope) DO NOTHING;

DELETE
FROM role_scopes rs
USING roles r, scopes s
WHERE rs.id_role = r.id_role
  AND rs.id_scope = s.id_scope
  AND r.name = 'USER'
  AND s.name IN ('users:read', 'user:read');

INSERT INTO users (id_user, email, enabled, is_admin, password, username)
VALUES (1, 'sebastiandossantosh@gmail.com', true, true,
        '$2a$10$DPjKRoWF6.xyfZxEGr7x5O1nXjPf/7LGdSZCq/HkCLNdmdojIAjVG', 'dossantosh'),
       (2, 'sebastiandossantosherrera@gmail.com', true, false,
        '$2a$10$DPjKRoWF6.xyfZxEGr7x5O1nXjPf/7LGdSZCq/HkCLNdmdojIAjVG', 'sevas') ON CONFLICT (id_user) DO NOTHING;

INSERT INTO users_roles (id_user, id_role)
SELECT 1, id_role
FROM roles
WHERE name = 'ADMIN'
UNION ALL
SELECT 2, id_role
FROM roles
WHERE name = 'USER' ON CONFLICT (id_user, id_role) DO NOTHING;

INSERT INTO users_modules (id_user, id_module)
SELECT 1, id_module
FROM modules ON CONFLICT (id_user, id_module) DO NOTHING;

INSERT INTO users_submodules (id_user, id_submodule)
SELECT 1, id_submodule
FROM submodules ON CONFLICT (id_user, id_submodule) DO NOTHING;

DELETE
FROM user_scope_grants usg
USING users u
WHERE usg.id_user = u.id_user
  AND u.username = 'sevas';

DELETE
FROM users_submodules us
USING users u
WHERE us.id_user = u.id_user
  AND u.username = 'sevas';

DELETE
FROM users_modules um
USING users u
WHERE um.id_user = u.id_user
  AND u.username = 'sevas';

SELECT setval(
               pg_get_serial_sequence('users', 'id_user'),
               COALESCE((SELECT MAX(id_user) FROM users), 0)
       );

INSERT INTO users (email, enabled, is_admin, password, username)
SELECT 'user' || gs || '@example.com',
       true,
       false,
       '$2a$10$FRYWPbBpdG3Snryo71URZuTk69hj7sn.Za2Z7Agc7xLbxBcExiAaq',
       'user_' || gs
FROM generate_series(10, 10010) gs ON CONFLICT (username) DO NOTHING;
