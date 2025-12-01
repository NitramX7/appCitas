@echo off
REM Script para ejecutar la aplicación Spring Boot con Java 21
REM Este script se ejecuta desde VS Code cuando presionas F5

setlocal enabledelayedexpansion

set JAVA_HOME=C:\Users\Gabriel\.jdk\jdk-21.0.8
set PATH=%JAVA_HOME%\bin;C:\Users\Gabriel\.maven\maven-3.9.11\bin;%PATH%

cd /d "%~dp0"

echo.
echo ====================================
echo Verificando Java 21...
echo ====================================
java -version

echo.
echo ====================================
echo Compilando y empaquetando...
echo ====================================
call mvn clean package -DskipTests
if errorlevel 1 (
    echo.
    echo Error en la compilacion
    pause
    exit /b 1
)

echo.
echo ====================================
echo Iniciando aplicacion Spring Boot...
echo ====================================
java -jar target\AD_U5_A3_CP-0.0.1-SNAPSHOT.jar

pause
