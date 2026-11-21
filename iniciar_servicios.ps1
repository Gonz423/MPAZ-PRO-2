Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Iniciando los 3 Microservicios Spring Boot - MPAZ PRO  " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

$baseDir = $PSScriptRoot

Write-Host "[1/3] Levantando ms-usuarios (Puerto 8081)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir\ms-usuarios'; ./mvnw spring-boot:run"

Write-Host "[2/3] Levantando ms-contenidos (Puerto 8082)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir\ms-contenidos'; ./mvnw spring-boot:run"

Write-Host "[3/3] Levantando ms-progreso (Puerto 8083)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir\ms-progreso'; ./mvnw spring-boot:run"

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host "  Swagger UI disponible en:                             " -ForegroundColor Green
Write-Host "  - ms-usuarios:   http://localhost:8081/swagger-ui.html" -ForegroundColor Green
Write-Host "  - ms-contenidos: http://localhost:8082/swagger-ui.html" -ForegroundColor Green
Write-Host "  - ms-progreso:   http://localhost:8083/swagger-ui.html" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
