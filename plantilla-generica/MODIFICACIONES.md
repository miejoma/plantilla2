# Cómo adaptar la plantilla y posibles modificaciones del examen

Regla de oro del taller: **no modifiques las clases de `security/`** (solo el `package`). Tu trabajo es proteger TUS endpoints con `@PreAuthorize`
y poner los roles de TU dominio en `data-inicial.sql`.

---
## 0. Orden de trabajo recomendado (examen)
1. Lee el enunciado y anota: entidades, cuál tiene las FK, campos obligatorios, roles, endpoints, mensajes/códigos HTTP, prefijo de variables.
2. Crea el proyecto (o copia esta plantilla) con el paquete base correcto; BD vacía creada.
3. `pom.xml` (ya incluye security + jjwt) y `application.properties` (BD + `jwt.secret`).
4. Entities -> Repositories -> DTOs -> Service -> Controller -> `@PreAuthorize`.
5. Levanta una vez -> detén -> `data-inicial.sql` (roles del dominio) -> levanta.
6. Prueba en Swagger/Postman: token -> cada endpoint -> negativos (sin token 401, rol incorrecto 403, validación 400, id inexistente 404).
7. Capturas para el Word: request + response de cada endpoint + tabla en la BD (+ una de 401 y otra de 403 si las piden).

---
## 1. Mapa de renombres (adaptar a TU dominio)
| En la plantilla | Qué es | Dónde se toca |
|---|---|---|
| `Principal` | Entidad principal (CRUD y con las FK) | entity, dto (Request/Response), repository, service, controller, SQL |
| `PadreA`, `PadreB` | Entidades "padre" (lado uno de las FK) | entity, repository, `copiarDatos` del service, SQL |
| `padreAId`, `padreBId` | Ids de las FK en el Request | `PrincipalRequest`, `copiarDatos` |
| `cantidad`, `monto`, `fecha`, `estado` | Campos de ejemplo de `Principal` | entity, Request, Response, service (`copiarDatos`, `convertir`), SQL |
| `Reporte*` / `reporteAgrupado` | Reporte (projection + response + query) | `ReporteProjection`, `ReporteResponse`, `PrincipalRepository`, service, controller |
| `/api/principal` | Ruta base del controller | `@RequestMapping` |
| `registrar/listar/actualizar/eliminar` | Rutas de cada operación | `@PostMapping`, `@GetMapping`... (copia el texto EXACTO del enunciado) |
| `e1_` / `e2_` | Prefijo de variables exigido | variables del service y controller (Buscar/Reemplazar) |
| `ADMIN`, `USUARIO` | Roles | `data-inicial.sql` + cada `@PreAuthorize` |
| `bddemo` | Nombre de la BD | `spring.datasource.url` |
| `com.example.demo` | Paquete base | IntelliJ: Shift+F6 sobre el paquete (cambia también `security/`) |
| tablas `padre_a`, `padre_b`, `principal` | Nombres de tabla | `@Table(name=...)` y el SQL de datos / nativas |

**Cómo renombrar rápido en IntelliJ:** clic derecho sobre la clase -> Refactor > Rename (Shift+F6) -> marca "search in comments"
para que también cambien los comentarios. Para atributos: Shift+F6 sobre el campo (actualiza getters/setters).
Los nombres de columnas en SQL son `snake_case` del atributo: `codigoPedido` -> `codigo_pedido`, FK `padre_a_id` -> `cliente_id`.
Si cambias un nombre de tabla/columna, actualiza también las consultas nativas y `data-inicial.sql`.

## 2. Variaciones de SEGURIDAD

### 2.1 Cambiar quién puede entrar a cada endpoint
```java
@PreAuthorize("hasRole('ADMIN')")                      // un solo rol
@PreAuthorize("hasAnyRole('ADMIN','USUARIO')")        // varios roles
@PreAuthorize("hasRole('A') or hasRole('B')")                  // equivalente con SpEL
@PreAuthorize("hasAuthority('ROLE_USUARIO')")                 // con prefijo explícito
@PreAuthorize("isAuthenticated()")                             // cualquier usuario con token válido
```
- `hasRole('X')` -> busca `ROLE_X`. Se escribe SIN `ROLE_` (el prefijo lo pone `JwtUserDetailsService`).
- El nombre debe ser idéntico al de la tabla `roles` (mayúsculas incluidas).

### 2.2 Proteger toda la clase y excepcionar un método
```java
@PreAuthorize("hasRole('ADMIN')")      // aplica a todos los métodos
@RestController
public class ... {
    @PreAuthorize("hasAnyRole('ADMIN','USUARIO')")   // este método lo sobreescribe
    @GetMapping("/listar") ...
}
```

### 2.3 Más roles / más usuarios (`data-inicial.sql`)
```sql
INSERT INTO roles(name) VALUES ('ANALISTA');
INSERT INTO users(username, password, enabled)
VALUES ('analista1', '$2a$10$pQeZla3780Hhi38rLnuRvuJuppfpE0EVeFQ5xFLEvEWKzNzpgzv4.', true);   -- clave 12345
INSERT INTO user_role(user_id, role_id)
VALUES ((SELECT id FROM users WHERE username='analista1'), (SELECT id FROM roles WHERE name='ANALISTA'));
```
- Un usuario con dos roles = dos filas en `user_role`.
- Para otra contraseña necesitas otro hash BCrypt; si dicen "la misma para todos", reutiliza el hash de arriba (12345).

### 2.4 Un endpoint público (sin token)
Es lo único que obligaría a tocar `WebSecurityConfig` (agrega una línea en `authorizeHttpRequests`, antes de `anyRequest()`):
```java
.requestMatchers(HttpMethod.GET, "/api/principal/listar").permitAll()
```
(import `org.springframework.http.HttpMethod`). Hazlo solo si el enunciado lo pide expresamente.

### 2.5 Saber quién hizo la petición
```java
@GetMapping("/mis-datos")
public String yo(Authentication auth) {          // import org.springframework.security.core.Authentication
    return auth.getName();                       // username del token
}
```

### 2.6 Códigos que debes reconocer
| Código | Significa | Quién lo genera |
|---|---|---|
| 401 | sin token / token inválido o expirado / login incorrecto | `JwtAuthenticationEntryPoint` |
| 403 | token válido pero el rol no alcanza | `@PreAuthorize` |
| 400 | JSON inválido (falla `@Valid`) | `ApiExceptionHandler` |
| 404 | id inexistente | `ApiExceptionHandler` |

---
## 3. Variaciones del REPORTE (lo que más cambia)
El reporte tiene 3 piezas: **query + projection (getters = alias) + response**. Para otro reporte cambias esas tres y el `.map(...)` del service.

### 3.1 Suma por padre A (solo un campo de agrupación)
```java
// repository/projection/MontoPadreAProjection.java
public interface MontoPadreAProjection { String getNombrePadreA(); BigDecimal getMontoTotal(); }

// PrincipalRepository
@Query(value = """
        SELECT a.nombre AS nombrePadreA, SUM(o.cantidad * o.monto) AS montoTotal
        FROM principal o
        INNER JOIN padre_a a ON a.id = o.padre_a_id
        GROUP BY a.nombre
        ORDER BY a.nombre
        """, nativeQuery = true)
List<MontoPadreAProjection> montoPorPadreA();
```

### 3.2 Contar / sumar / promediar (tipos de retorno en el getter)
| SQL | Tipo del getter |
|---|---|
| `COUNT(*)` | `Long` |
| `SUM(o.cantidad)` (enteros) | `Long` |
| `SUM(o.cantidad * o.monto)`, `AVG(...)`, `SUM(monto)` | `BigDecimal` |
| `MAX(o.fecha)` | `LocalDate` |
| mes: `CAST(EXTRACT(MONTH FROM o.fecha) AS INTEGER)` | `Integer` |
Si el tipo no coincide: error de conversión al llamar al reporte.

### 3.3 Con filtros (parámetros)
```java
@Query(value = """
        SELECT t.categoria AS categoriaPadreB, COUNT(*) AS cantidad
        FROM principal o INNER JOIN padre_b t ON t.id = o.padre_b_id
        WHERE o.fecha BETWEEN :inicio AND :fin AND o.estado = true
        GROUP BY t.categoria
        """, nativeQuery = true)
List<CantidadCategoriaProjection> cantidadEntre(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
```
Controller: `@GetMapping("/entreFechas") public List<...> entre(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin)`
Llamada: `/api/principal/entreFechas?inicio=2025-05-01&fin=2025-05-31`

### 3.4 Con HAVING (solo los que superan un monto)
```sql
GROUP BY a.nombre HAVING SUM(o.cantidad * o.monto) > :minimo
```

### 3.5 Alternativa con JPQL y DTO directo (sin projection)
```java
@Query("""
        SELECT new com.example.demo.dto.ReporteResponse(a.nombre, t.categoria, SUM(o.cantidad * o.monto))
        FROM Principal o JOIN o.padre_a a JOIN o.padre_b t
        GROUP BY a.nombre, t.categoria
        """)
List<ReporteResponse> reporteJpql();
```
(JPQL usa nombres de CLASES y ATRIBUTOS, no de tablas.) Se puede devolver directo desde el service sin `.map`.

### 3.6 Métodos derivados (Spring arma la consulta por el nombre)
```java
List<Principal> findByEstadoTrue();
List<Principal> findByFechaBetween(LocalDate inicio, LocalDate fin);
List<Principal> findByPadreAId(Long padreAId);
List<Principal> findByMontoGreaterThan(BigDecimal minimo);
boolean existsByEmail(String email);          // en PadreARepository
long countByEstado(Boolean estado);
```
Para convertirlos: `.stream().map(this::convertir).toList()` dentro del service (por `open-in-view=false`, el mapeo se hace dentro del service).

---
## 4. Variaciones de ENTIDADES / DTOs
- **Nuevo campo obligatorio:** entity `@Column(nullable = false) private String x;` + Request `@NotBlank String x` (texto) / `@NotNull` (otros) + Response + `copiarDatos` + `convertir`.
- **Campo opcional:** sin `@Column(nullable=false)` y sin `@NotNull`; en el service usa un valor por defecto si llega `null`.
- **Único:** `@Column(unique = true)`.
- **Validaciones útiles:** `@NotBlank`, `@Size(min=3,max=50)`, `@Email`, `@Min(1)`, `@Max(24)`, `@Positive`, `@PastOrPresent` (fechas), `@Pattern(regexp="A|B|C")`.
- **Estado con valores fijos (PENDIENTE/ENTREGADO...):** `String` + `@Pattern(regexp = "PENDIENTE|ENTREGADO|CANCELADO")` en el Request.
- **Dinero:** `BigDecimal` con `@Column(precision = 10, scale = 2)`. Si el enunciado exige `Double`, cámbialo en entity/DTO y la projection (`Double`).
- **Otra FK / otra entidad padre:** nuevo `@ManyToOne` + `@JoinColumn(name="x_id", nullable=false)`; `xId` en Request; `findById` en `copiarDatos`; datos de prueba en el SQL.
- **Solo una FK:** quita el segundo `@ManyToOne`, su repositorio y su línea en `copiarDatos`.
- **Entidad padre con CRUD propio:** copia el patrón Entity/Repository/DTO/Service/Controller de Principal.

---
## 5. Variaciones de ENDPOINTS
### 5.1 GET por id
```java
// Service
@Transactional(readOnly = true)
public PrincipalResponse buscar(Long e2_id) {
    return convertir(e2_principalRepository.findById(e2_id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe el registro principal con id " + e2_id)));
}
// Controller
@PreAuthorize("hasAnyRole('ADMIN','USUARIO')")
@GetMapping("/buscar/{id}")
public PrincipalResponse buscar(@PathVariable("id") Long e1_id) { return e1_principalService.buscar(e1_id); }
```
### 5.2 Listar por una FK
`@GetMapping("/porPadreA/{id}")` -> `repository.findByPadreAId(id)` -> `.stream().map(this::convertir).toList()`.
### 5.3 Cambiar solo el estado (PATCH)
```java
@PreAuthorize("hasRole('ADMIN')")
@PatchMapping("/estado/{id}")
public PrincipalResponse cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) { ... }
```
En el service: buscar -> `setEstado(estado)` -> `save`.
### 5.4 Códigos HTTP
Crear -> 201 (`ResponseEntity.status(HttpStatus.CREATED)`); eliminar -> 204 (`@ResponseStatus(NO_CONTENT)`); lo demás -> 200.
### 5.5 Mensaje en vez de objeto
Si piden "mensaje + id": crea un `record MensajeResponse(Long id, String mensaje)` y devuélvelo desde el service.

---
## 6. Variaciones de EXCEPCIONES
- **Registro duplicado (409):** crea `ConflictoException extends RuntimeException` y en `ApiExceptionHandler`:
```java
@ExceptionHandler(ConflictoException.class)
public ResponseEntity<Map<String, Object>> conflicto(ConflictoException e) { return respuesta(HttpStatus.CONFLICT, e.getMessage()); }
```
y en el service: `if (repo.existsByEmail(x)) throw new ConflictoException("Ya existe...");`
- **Regla de negocio (400):** igual, con `HttpStatus.BAD_REQUEST`.
- ⚠ **Nunca** agregues un handler para `Exception`/`RuntimeException`: se tragaría el 401/403 de Spring Security.

---
## 7. Variaciones de pom / properties
- La plantilla usa los mismos `pom.xml` y versiones del ejercicio base (Boot 4.1.1, Java 21, springdoc 3.0.0). Si tu profesor da otro `pom`, solo agrega las dependencias de seguridad:
  `spring-boot-starter-security` + `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (0.12.5; impl y jackson con `<scope>runtime</scope>`).
- `jwt.secret`: hexadecimal, 64 caracteres, sin espacios. (`openssl rand -hex 32`).
- Puerto distinto: `server.port=8081`.
- Ver el SQL en consola: `spring.jpa.show-sql=true`.
- Recrear tablas desde cero: borra y vuelve a crear la BD (o `ddl-auto=create` UNA vez, luego vuelve a `update`) y re-ejecuta `data-inicial.sql`.

---
## 8. Errores típicos
| Síntoma | Causa probable |
|---|---|
| `relation "users" does not exist` al ejecutar el SQL | Ejecutaste el SQL antes de levantar la app una vez |
| 500 al hacer login con `Illegal hexadecimal character` / `Odd number of characters` | `jwt.secret` no es hex válido (solo 0-9, a-f; longitud par) |
| 403 con un usuario que debería poder | Nombre del rol distinto entre `roles` y `@PreAuthorize`, o falta la fila en `user_role`, o escribiste `ROLE_` dentro de `hasRole` |
| 401 en un endpoint protegido | Falta el token / no diste Authorize en Swagger / token expirado (5 h) / pegaste "Bearer " dentro del campo de Swagger (solo va el token) |
| 401 en el login | username o password incorrectos o llaves del JSON distintas a `username` y `password` |
| `duplicate key value violates unique constraint` | Repetiste un campo `unique` (email, username) |
| `LazyInitializationException` | Convertiste a DTO fuera del service (con `open-in-view=false` debe hacerse dentro) |
| `No qualifying bean` en una clase de `security` | La carpeta `security/` quedó fuera del paquete base de la app |
| `Could not convert ... to ...` en un reporte | El tipo del getter de la projection no coincide con el tipo SQL (ver tabla 3.2) |
| 400 al enviar fecha | Formato debe ser `yyyy-MM-dd` (2025-06-01) |
| Postman 401 pese a tener token | Pestaña Authorization -> Bearer Token (o header `Authorization: Bearer <token>`) |

---
## 9. Evidencias del Word (según tu documento de evidencias)
Un bloque por operación (REGISTRAR, LISTAR, ACTUALIZAR, ELIMINAR, y el reporte si lo piden) con: request (URL + JSON), response (código + cuerpo)
y captura de la tabla en la BD. Con seguridad, añade en cada request que el token fue enviado (Authorize en Swagger) y, si se pide, una captura de 401 (sin token) y otra de 403 (rol incorrecto).
