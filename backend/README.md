# Backend Microservicios — MPAZ PRO 2.º

Arquitectura de microservicios desarrollada con **Spring Boot 3** y **Java 17/21** para la plataforma educativa Marcela Paz (San Bernardo).

## Microservicios

| Microservicio | Puerto | Base de Datos | Endpoints Principales | Swagger UI |
|---|---|---|---|---|
| **ms-contenidos** | 8082 | H2 (in-memory) | `/api/unidades`, `/api/ejercicios` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| **ms-progreso** | 8083 | H2 (in-memory) | `/api/resultados`, `/api/observaciones` | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |

## Ejecución

### Opción 1: Scripts de inicio rápido (Recomendado)
- **Windows Batch**: Doble clic en `iniciar_servicios.bat`
- **PowerShell**: Ejecutar `./iniciar_servicios.ps1`

### Opción 2: Docker Compose
```bash
docker-compose up --build
```

### Opción 3: Maven independiente
```bash
cd ms-contenidos && ./mvnw spring-boot:run
cd ms-progreso && ./mvnw spring-boot:run
```
