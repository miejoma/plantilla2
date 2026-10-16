-- ============================================================================
-- Ejecutar DESPUÉS de levantar la app UNA VEZ (para que Hibernate cree las tablas) y detenerla.
-- ============================================================================

-- 1) ROLES de TU dominio (nombre SIN 'ROLE_'; debe coincidir con @PreAuthorize)
INSERT INTO roles(name) VALUES ('ADMIN'), ('USUARIO');

-- 2) USUARIOS. Contraseña de ambos = 12345 (hash BCrypt)
INSERT INTO users(username, password, enabled) VALUES
  ('admin1',    '$2a$10$pQeZla3780Hhi38rLnuRvuJuppfpE0EVeFQ5xFLEvEWKzNzpgzv4.', true),
  ('usuario1', '$2a$10$pQeZla3780Hhi38rLnuRvuJuppfpE0EVeFQ5xFLEvEWKzNzpgzv4.', true);

-- 3) USUARIO <-> ROL
INSERT INTO user_role(user_id, role_id) VALUES
  ((SELECT id FROM users WHERE username='admin1'),    (SELECT id FROM roles WHERE name='ADMIN')),
  ((SELECT id FROM users WHERE username='usuario1'), (SELECT id FROM roles WHERE name='USUARIO'));

-- 4) DATOS DE PRUEBA del dominio (las FK necesitan filas existentes: borra/ajusta según tus entidades)
INSERT INTO padre_a(nombre, email, telefono) VALUES
  ('Padre A 1', 'a1@mail.com', '999111222'),
  ('Padre A 2', 'a2@mail.com', NULL);

INSERT INTO padre_b(nombre, categoria, ubicacion) VALUES
  ('Padre B 1', 'CATEGORIA_X', 'Ubicacion 1'),
  ('Padre B 2', 'CATEGORIA_Y', 'Ubicacion 2');

INSERT INTO principal(cantidad, fecha, monto, estado, padre_a_id, padre_b_id) VALUES
  (3, '2025-05-10', 50.00, true,  1, 1),
  (2, '2025-05-11', 60.00, true,  1, 2),
  (4, '2025-05-12', 45.50, false, 2, 1);

-- Verificar usuarios y roles:
-- SELECT u.username, r.name FROM users u JOIN user_role ur ON ur.user_id=u.id JOIN roles r ON r.id=ur.role_id;
