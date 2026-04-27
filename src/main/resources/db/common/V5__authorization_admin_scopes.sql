INSERT INTO scopes (name, description)
VALUES ('role:read', 'Read roles'),
       ('role:assign', 'Assign roles to users'),
       ('scope:read', 'Read scopes'),
       ('scope:assign', 'Assign direct scopes to users') ON CONFLICT (name) DO NOTHING;

INSERT INTO role_scopes (id_role, id_scope)
SELECT r.id_role, s.id_scope
FROM roles r
CROSS JOIN scopes s
WHERE r.name = 'ADMIN'
  AND s.name IN ('role:read', 'role:assign', 'scope:read', 'scope:assign') ON CONFLICT (id_role, id_scope) DO NOTHING;
