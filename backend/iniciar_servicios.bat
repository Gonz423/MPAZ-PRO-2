@echo off
title Lanzador de Microservicios - MPAZ PRO 2.o
echo ========================================================
echo   Iniciando Microservicios Spring Boot - MPAZ PRO 2.o
echo ========================================================
echo.

echo [1/2] Levantando ms-contenidos (Puerto 8082)...
start "ms-contenidos - 8082" cmd /k "cd /d "%~dp0ms-contenidos" && mvnw.cmd spring-boot:run"

echo [2/2] Levantando ms-progreso (Puerto 8083)...
start "ms-progreso - 8083" cmd /k "cd /d "%~dp0ms-progreso" && mvnw.cmd spring-boot:run"

echo.
echo ========================================================
echo   Microservicios activos:
echo   - ms-contenidos: http://localhost:8082/swagger-ui.html
echo   - ms-progreso:   http://localhost:8083/swagger-ui.html
echo ========================================================
pause
