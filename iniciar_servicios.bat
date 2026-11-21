@echo off
title Lanzador de Microservicios - MPAZ PRO 2.o
echo ========================================================
echo   Iniciando los 3 Microservicios Spring Boot - MPAZ PRO
echo ========================================================
echo.

echo [1/3] Levantando ms-usuarios (Puerto 8081)...
start "ms-usuarios - 8081" cmd /k "cd /d "%~dp0ms-usuarios" && mvnw.cmd spring-boot:run"

echo [2/3] Levantando ms-contenidos (Puerto 8082)...
start "ms-contenidos - 8082" cmd /k "cd /d "%~dp0ms-contenidos" && mvnw.cmd spring-boot:run"

echo [3/3] Levantando ms-progreso (Puerto 8083)...
start "ms-progreso - 8083" cmd /k "cd /d "%~dp0ms-progreso" && mvnw.cmd spring-boot:run"

echo.
echo ========================================================
echo   Swagger UI disponible en:
echo   - ms-usuarios:   http://localhost:8081/swagger-ui.html
echo   - ms-contenidos: http://localhost:8082/swagger-ui.html
echo   - ms-progreso:   http://localhost:8083/swagger-ui.html
echo ========================================================
pause
