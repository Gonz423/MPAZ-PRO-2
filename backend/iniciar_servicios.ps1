Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Iniciando Microservicios Spring Boot - MPAZ PRO 2.o     " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

$baseDir = $PSScriptRoot

Write-Host "[1/2] Levantando ms-contenidos (Puerto 8082)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir\ms-contenidos'; ./mvnw spring-boot:run"

Write-Host "[2/2] Levantando ms-progreso (Puerto 8083)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir\ms-progreso'; ./mvnw spring-boot:run"

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host "  Swagger UI disponible en:                             " -ForegroundColor Green
Write-Host "  - ms-contenidos: http://localhost:8082/swagger-ui.html" -ForegroundColor Green
Write-Host "  - ms-progreso:   http://localhost:8083/swagger-ui.html" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
