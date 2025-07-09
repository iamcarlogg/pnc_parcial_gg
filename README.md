# Parcial Final Programación N-Capas – (Seguridad con Spring Security + JWT)

Este repositorio contiene un proyecto para evaluar y practicar los conceptos de seguridad en aplicaciones Spring Boot usando JWT, roles y Docker.

### Estudiantes
- **Nombre del estudiante 1**: Carlo Enrique Guerra Vega - 00052220
- **Nombre del estudiante 2**: Kelvin Rodrigo Iraheta Morales - 00083121
- Sección: 01
---

## Sistema de Soporte Técnico

### Descripción
Simula un sistema donde los usuarios pueden crear solicitudes de soporte (tickets) y los técnicos pueden gestionarlas. Actualmente **no tiene seguridad implementada**.

Su tarea es **agregar autenticación y autorización** utilizando **Spring Security + JWT**, y contenerizar la aplicación con Docker.

### Requisitos generales

- Proyecto funcional al ser clonado y ejecutado con Docker.
- Uso de PostgreSQL (ya incluido en docker-compose).
- Seguridad implementada con JWT.
- Roles `USER` y `TECH`.
- Acceso restringido según el rol del usuario.
- Evidencia de funcionamiento (colección de Postman/Insomnia/Bruno o capturas de pantalla).

**Nota: El proyecto ya tiene una estructura básica de Spring Boot con endpoints funcionales para manejar tickets. No es necesario modificar la lógica de negocio, solo agregar seguridad. Ademas se inclye un postman collection para probar los endpoints. **

_Si van a crear mas endpoints como el login o registrarse recuerden actualizar postman/insomnia/bruno collection_

### Partes de desarrollo

#### Parte 1: Implementar login con JWT
- [ ] Crear endpoint `/auth/login`.
- [ ] Validar usuario y contraseña (puede estar en memoria o en BD).
- [ ] Retornar JWT firmado.

#### Parte 2: Configurar filtros y validación del token
- [ ] Crear filtro para validar el token en cada solicitud.
- [ ] Extraer usuario desde el JWT.
- [ ] Añadir a contexto de seguridad de Spring.

#### Parte 3: Proteger endpoints con Spring Security
- [ ] Permitir solo el acceso al login sin token.
- [ ] Proteger todos los demás endpoints.
- [ ] Manejar errores de autorización adecuadamente.

#### Parte 4: Aplicar roles a los endpoints

| Rol   | Acceso permitido                                 |
|--------|--------------------------------------------------|
| USER  | Crear tickets, ver solo sus tickets              |
| TECH  | Ver todos los tickets, actualizar estado         |

- [ ] Usar `@PreAuthorize` o reglas en el `SecurityFilterChain`.
- [ ] Validar que un USER solo vea sus tickets.
- [ ] Validar que solo un TECH pueda modificar tickets.

#### Parte 5: Agregar Docker
- [ ] `Dockerfile` funcional para la aplicación.
- [ ] `docker-compose.yml` que levante la app y la base de datos.
- [ ] Documentar cómo levantar el entorno (`docker compose up`).

#### Parte 6: Evidencia de pruebas
- [ ] Probar todos los flujos con Postman/Insomnia/Bruno.
- [ ] Mostrar que los roles se comportan correctamente.
- [ ] Incluir usuarios de prueba (`user`, `tech`) y contraseñas.

## Documentación por Partes

### Parte 1: Implementar login con JWT

- **Endpoint:** `POST /auth/login`
- **Request:** JSON con `username` y `password`.
- **Validación:** Credenciales verificadas contra BD (o en memoria).
- **Respuesta:** JWT firmado en el cuerpo:
```json
{
  "token": "<JWT>"
}
```
- **Detalles de implementación:**
  - Controlador `AuthController` con método `login()`.
  - Servicio `AuthService` que autentica y genera el token usando `io.jsonwebtoken.Jwts`.

---

### Parte 2: Configurar filtros y validación del token

- **Filtro JWT:** Clase `JwtAuthenticationFilter` extiende `OncePerRequestFilter`.
  - Extrae el encabezado `Authorization: Bearer <token>`.
  - Valida firma y fecha usando la clave secreta.
  - Obtiene el `username` del token y carga `UserDetails`.
  - Construye `UsernamePasswordAuthenticationToken` y lo inyecta en el contexto de Spring Security.
- **Registro del filtro:** En la configuración de seguridad (`SecurityConfig`), se añade:
```java
http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
```

---

### Parte 3: Proteger endpoints con Spring Security

- **Permitir acceso público:** `/auth/login` sin token.
- **Protección global:** Todos los demás endpoints requieren autenticación.
- **Manejo de errores:** Configuración de `AuthenticationEntryPoint` y `AccessDeniedHandler` para respuestas 401 y 403 con mensajes JSON adecuados:
```java
http
  .authorizeHttpRequests()
    .requestMatchers("/auth/login").permitAll()
    .anyRequest().authenticated()
  .and()
  .exceptionHandling()
    .authenticationEntryPoint(restAuthenticationEntryPoint)
    .accessDeniedHandler(restAccessDeniedHandler);
```

---

### Parte 4: Aplicar roles a los endpoints

| Rol  | Acciones permitidas                         |
|------|---------------------------------------------|
| USER | Crear tickets, ver solo sus propios tickets |
| TECH | Ver todos los tickets, actualizar estado    |

- **Con anotaciones `@PreAuthorize`:**
```java
@PreAuthorize("hasRole('USER')")
public Ticket createTicket(...) { ... }

@PreAuthorize("hasRole('TECH')")
public Ticket updateTicketStatus(...) { ... }
```

- **O con reglas en `SecurityFilterChain`:**
```java
http
  .authorizeHttpRequests()
    .requestMatchers(HttpMethod.POST, "/tickets").hasRole("USER")
    .requestMatchers(HttpMethod.PUT, "/tickets").hasRole("TECH");
```

---

### Parte 5: Agregar Docker

1. **Dockerfile:** Basado en `eclipse-temurin:21-jre-jammy`, copia el `.jar` y expone el puerto 8080.

2. **docker-compose.yml:** Define servicios:
   - **app:** construye desde el Dockerfile.
   - **db:** usa `postgres:17`, volumen para persistencia, variables de entorno:
     ```yaml
     POSTGRES_DB: supportdb
     POSTGRES_USER: postgres
     POSTGRES_PASSWORD: root
     ```

3. **Levantamiento del entorno:**
```bash
docker compose up --build
```

---

### Parte 6: Evidencia de pruebas

- **Usuarios de prueba:**
  - USER / user123
  - TECH / tech123

- **Flujos validados:**
  1. Login exitoso y obtención de JWT.
  2. Creación de ticket (USER) – verifica propietario.
  3. Intento de creación de ticket sin token – recibe 401.
  4. Listar tickets (USER) – solo propios.
  5. Actualizar estado (TECH) – éxito.
  6. Intento de actualización por USER – recibe 403.

- **Herramienta usada:** Postman o Insomnia.  
  Se incluyen colecciones `.json` en la carpeta `/evidencias` del repositorio.

---

**En Recursos hay una coleccion de postman que utilizamos**
