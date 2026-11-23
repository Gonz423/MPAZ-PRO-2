# Backend Microservicios — MPAZ PRO 2.º

Arquitectura de microservicios desarrollada con **Spring Boot 3**, **Java 17/21**, persistencia **H2 Database** y seguridad **Spring Security + JWT (JJWT 0.12.6)** para la plataforma educativa Marcela Paz (San Bernardo).

---

## 🏗️ Catálogo de Microservicios

| Microservicio | Puerto | Base de Datos | Endpoints Principales | Swagger UI |
|---|---|---|---|---|
| **ms-usuarios** | **8081** | H2 (`jdbc:h2:mem:usuariosdb`) | `/api/auth/**`, `/api/usuarios/**` | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **ms-contenidos** | **8082** | H2 (`jdbc:h2:mem:mpaz_contenidos`) | `/api/unidades`, `/api/ejercicios` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| **ms-progreso** | **8083** | H2 (`jdbc:h2:mem:mpaz_progreso`) | `/api/resultados`, `/api/observaciones` | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |

---

## 🔐 Detalle de `ms-usuarios` (Puerto 8081)

Microservicio responsable de:
- Autenticación segura mediante credenciales y tokens JWT (algoritmo HMAC-SHA256).
- Cifrado de contraseñas con **BCryptPasswordEncoder**.
- Gestión de perfiles de usuario (`ESTUDIANTE`, `DOCENTE`, `UTP`).
- Borrado lógico (`activo = false`) y filtros de búsqueda.
- Consola H2 en [http://localhost:8081/h2-console](http://localhost:8081/h2-console).

### Endpoints REST

#### 1. Autenticación (`/api/auth`)
- `POST /api/auth/login`: Autentica credenciales y genera token JWT.
  ```json
  // Request
  { "username": "estudiante", "password": "1234" }

  // Response 200
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "id": 1,
    "username": "estudiante",
    "nombreCompleto": "Mateo Silva",
    "rol": "ESTUDIANTE",
    "curso": "2° Básico A",
    "tokenType": "Bearer"
  }
  ```
- `POST /api/auth/register`: Registra un nuevo usuario con contraseña cifrada en BCrypt.
- `GET /api/auth/validate`: Valida la firma y vigencia del token JWT.

#### 2. Gestión de Usuarios (`/api/usuarios`)
- `GET /api/usuarios`: Listado de usuarios activos (`?todos=true` para incluir inactivos).
- `GET /api/usuarios/{id}`: Detalle de usuario por identificador.
- `GET /api/usuarios/rol/{rol}`: Filtro por rol (`ESTUDIANTE`, `DOCENTE`, `UTP`).
- `PUT /api/usuarios/{id}`: Actualización de perfil (nombre, curso, email, rol, estado, clave).
- `DELETE /api/usuarios/{id}`: Desactivación / borrado lógico de usuario.

### Usuarios Semilla Iniciales (`DataInitializer`)

| Usuario | Contraseña | Rol | Nombre Completo | Curso |
|---|---|---|---|---|
| `estudiante` | `1234` | `ESTUDIANTE` | Mateo Silva | 2° Básico A |
| `docente` | `1234` | `DOCENTE` | Profesora Marcela | 2° Básico A |
| `utp` | `1234` | `UTP` | Jefe UTP | Coordinación Académica |

*Nota: Todas las contraseñas se almacenan con hash BCrypt.*

---

## 🚀 Modos de Ejecución

### Opción 1: Scripts de inicio rápido (Recomendado en Windows)
- **Batch**: Doble clic en `iniciar_servicios.bat`
- **PowerShell**: Ejecutar `./iniciar_servicios.ps1`

### Opción 2: Docker Compose (Contenedores)
```bash
docker-compose up --build
```

### Opción 3: Maven independiente (desde cada carpeta)
```bash
cd ms-usuarios && ./mvnw spring-boot:run
cd ms-contenidos && ./mvnw spring-boot:run
cd ms-progreso && ./mvnw spring-boot:run
```
