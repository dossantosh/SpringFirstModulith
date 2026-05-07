INSERT INTO roles (name)
VALUES ('SYSTEMS'),
       ('PERFUMES') ON CONFLICT (name) DO NOTHING;

INSERT INTO modules (name, navigation_key, label, icon, sort_order)
VALUES ('SYSTEMS', 'systems', 'Sistemas', 'settings', 10),
       ('PERFUMES', 'perfumes', 'Perfumes', 'local_florist', 20)
ON CONFLICT (name) DO UPDATE
SET navigation_key = EXCLUDED.navigation_key,
    label = EXCLUDED.label,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order;

INSERT INTO submodules (name, id_module, navigation_key, label, icon, route, disabled, hint, sort_order)
SELECT 'USERS_SEARCH', id_module, 'users_search', 'Usuarios', 'group', '/users/search', false, NULL, 10
FROM modules
WHERE name = 'SYSTEMS'
ON CONFLICT (name, id_module) DO UPDATE
SET navigation_key = EXCLUDED.navigation_key,
    label = EXCLUDED.label,
    icon = EXCLUDED.icon,
    route = EXCLUDED.route,
    disabled = EXCLUDED.disabled,
    hint = EXCLUDED.hint,
    sort_order = EXCLUDED.sort_order;

INSERT INTO submodules (name, id_module, navigation_key, label, icon, route, disabled, hint, sort_order)
SELECT 'PERFUMES_CATALOG', id_module, 'perfumes_catalog', 'Catálogo', 'local_florist', '/perfumes/catalog', true,
       'Módulo previsto para próximas fases', 10
FROM modules
WHERE name = 'PERFUMES'
ON CONFLICT (name, id_module) DO UPDATE
SET navigation_key = EXCLUDED.navigation_key,
    label = EXCLUDED.label,
    icon = EXCLUDED.icon,
    route = EXCLUDED.route,
    disabled = EXCLUDED.disabled,
    hint = EXCLUDED.hint,
    sort_order = EXCLUDED.sort_order;

INSERT INTO scopes (name, description)
VALUES ('systems:read', 'Read systems module'),
       ('systems:write', 'Write systems module'),
       ('perfumes:read', 'Read perfumes module'),
       ('perfumes:write', 'Write perfumes module') ON CONFLICT (name) DO NOTHING;

INSERT INTO role_scopes (id_role, id_scope)
SELECT r.id_role, s.id_scope
FROM roles r
JOIN scopes s ON s.name IN ('systems:read', 'systems:write')
WHERE r.name = 'SYSTEMS' ON CONFLICT (id_role, id_scope) DO NOTHING;

INSERT INTO role_scopes (id_role, id_scope)
SELECT r.id_role, s.id_scope
FROM roles r
JOIN scopes s ON s.name IN ('perfumes:read', 'perfumes:write')
WHERE r.name = 'PERFUMES' ON CONFLICT (id_role, id_scope) DO NOTHING;

INSERT INTO submodule_required_scopes (id_submodule, id_scope)
SELECT sm.id_submodule, sc.id_scope
FROM submodules sm
JOIN modules m ON m.id_module = sm.id_module
JOIN scopes sc ON sc.name = 'systems:read'
WHERE m.name = 'SYSTEMS'
  AND sm.name = 'USERS_SEARCH' ON CONFLICT (id_submodule, id_scope) DO NOTHING;

INSERT INTO submodule_required_scopes (id_submodule, id_scope)
SELECT sm.id_submodule, sc.id_scope
FROM submodules sm
JOIN modules m ON m.id_module = sm.id_module
JOIN scopes sc ON sc.name = 'perfumes:read'
WHERE m.name = 'PERFUMES'
  AND sm.name = 'PERFUMES_CATALOG' ON CONFLICT (id_submodule, id_scope) DO NOTHING;

INSERT INTO users (id_user, email, enabled, is_admin, password, username)
VALUES (1, 'sebastiandossantosh@gmail.com', true, true,
        '$2a$10$DPjKRoWF6.xyfZxEGr7x5O1nXjPf/7LGdSZCq/HkCLNdmdojIAjVG', 'dossantosh'),
       (2, 'sebastiandossantosherrera@gmail.com', true, false,
        '$2a$10$DPjKRoWF6.xyfZxEGr7x5O1nXjPf/7LGdSZCq/HkCLNdmdojIAjVG', 'sevas') ON CONFLICT (id_user) DO NOTHING;

INSERT INTO users_roles (id_user, id_role)
SELECT 1, id_role
FROM roles
WHERE name = 'SYSTEMS'
UNION ALL
SELECT 1, id_role
FROM roles
WHERE name = 'PERFUMES'
UNION ALL
SELECT 2, id_role
FROM roles
WHERE name = 'SYSTEMS' ON CONFLICT (id_user, id_role) DO NOTHING;

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
