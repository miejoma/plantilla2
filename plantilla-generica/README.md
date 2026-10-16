# Plantilla genérica API REST + Spring Security (JWT)

Plantilla para cualquier ejercicio tipo PC: **CRUD de una entidad principal con 2 llaves foráneas + un reporte agregado**,
protegida con la capa de seguridad del taller de la semana 4 (JWT + roles). Todo está comentado.

Los nombres son neutros para que los reemplaces por los de tu dominio (ver `MODIFICACIONES.md`, sección 1):

| Plantilla | Qué representa | Ejemplos de dominio |
|---|---|---|
| `Principal` | Entidad principal (CRUD + FK a las dos de abajo) | Pago, Venta, Reserva, Observación, Pedido |
| `PadreA` | Primera entidad "padre" (lado uno de la FK) | Cliente, Astrónomo, Paciente |
| `PadreB` | Segunda entidad "padre" | Producto, Telescopio, Doctor |

Stack: Spring Boot 4.1.1 · Java 21 · PostgreSQL · springdoc 3.0.0 · jjwt 0.12.5. Paquete base: `com.example.demo`
(si lo cambias, TODO debe quedar dentro de él, incluido `security/`).

## Pasos para levantarlo
1. Crea una BD vacía en PostgreSQL (el nombre debe coincidir con `spring.datasource.url`) y ajusta usuario/clave en `application.properties`.
2. Ejecuta la app UNA vez (Hibernate crea las tablas `padre_a`, `padre_b`, `principal`, `users`, `roles`, `user_role`) y detenla.
3. Ejecuta `data-inicial.sql` en tu cliente SQL (roles, usuarios con clave 12345 y datos de prueba).
4. Vuelve a ejecutar la app. Swagger: http://localhost:8080/swagger-ui.html
5. `POST /ingresos` con `{"username":"admin1","password":"12345"}` -> copia el token -> botón **Authorize** -> prueba.
   (O usa `pruebas.http` en IntelliJ: trae todas las peticiones, incluidas las negativas 401/403/400/404.)

## Permisos de ejemplo
| Endpoint | Rol(es) |
|---|---|
| POST /api/principal/registrar | ADMIN, USUARIO |
| GET /api/principal/listar | ADMIN, USUARIO |
| PUT /api/principal/actualizar/{id} | ADMIN |
| DELETE /api/principal/eliminar/{id} | ADMIN |
| GET /api/principal/reporteAgrupado | ADMIN |
| POST /ingresos | público |

Usuarios: `admin1` (ADMIN) y `usuario1` (USUARIO), clave `12345`.

## Estructura
`entity` · `dto` · `repository` (+`projection`) · `service` · `controller` · `exception` · `config` (Swagger+JWT) · `security` (10 clases del taller:
solo cambió el `package`, el código es idéntico; se agregaron comentarios).

👉 Lee **MODIFICACIONES.md**: cómo adaptar la plantilla a tu dominio y las variaciones posibles del examen.
