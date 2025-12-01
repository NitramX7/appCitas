# Script para ejecutar la aplicación Spring Boot con Java 21
# Uso: .\run-app.ps1

# Configurar variables de entorno
$env:JAVA_HOME = 'C:\Users\Gabriel\.jdk\jdk-21.0.8'
$env:Path = "C:\Users\Gabriel\.jdk\jdk-21.0.8\bin;" + $env:Path

# Usar la ruta actual del directorio
$projectRoot = 'C:\Users\Gabriel\Documents\appCitas\appCitas\appCitas\appCitas(spring)'
$targetDir = Join-Path $projectRoot 'target'

# Verificar que Java 21 está disponible
Write-Host "Verificando Java 21..." -ForegroundColor Cyan
java -version

Write-Host "`nBuscando JAR compilado..." -ForegroundColor Cyan
$jarFile = Get-ChildItem -Path $targetDir -Filter "AD_U5_A3_CP-0.0.1-SNAPSHOT.jar" -ErrorAction SilentlyContinue | Select-Object -First 1

if (-not $jarFile) {
    Write-Host "Error: No se encontró el JAR compilado en $targetDir" -ForegroundColor Red
    Write-Host "Archivo buscado: AD_U5_A3_CP-0.0.1-SNAPSHOT.jar" -ForegroundColor Yellow
    Get-ChildItem -Path $targetDir -Filter "*.jar" | ForEach-Object { Write-Host "  Encontrado: $($_.Name)" }
    exit 1
}

Write-Host "JAR encontrado: $($jarFile.FullName)" -ForegroundColor Green
Write-Host "`nIniciando aplicación Spring Boot..." -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Yellow

# Ejecutar la aplicación
& java -jar $jarFile.FullName

Write-Host "======================================" -ForegroundColor Yellow
Write-Host "Aplicación finalizada." -ForegroundColor Cyan
