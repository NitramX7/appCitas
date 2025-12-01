# Guía de Ejecución - AppCitas con Java 21

## 🚀 Opción 1: Ejecutar desde Terminal de PowerShell

```powershell
# Compilar el proyecto
$env:JAVA_HOME = 'C:\Users\Gabriel\.jdk\jdk-21.0.8'
$env:Path = "C:\Users\Gabriel\.maven\maven-3.9.11\bin;" + $env:Path
mvn clean package

# Ejecutar la aplicación
java -jar target/AD_U5_A3_CP-0.0.1-SNAPSHOT.jar
```

## 🚀 Opción 2: Ejecutar Script de PowerShell (Recomendado)

```powershell
.\run-app.ps1
```

Este script:
- Configura automáticamente JAVA_HOME con Java 21
- Verifica que Java 21 esté disponible
- Encuentra el JAR compilado
- Inicia la aplicación

## 🚀 Opción 3: Usar Tareas de VS Code

### Compilar el Proyecto
```
Ctrl+Shift+B  (o F1 → "Run Build Task")
```

### Ejecutar la Aplicación
```
F1 → "Run Task" → "Run Spring Boot App (Java 21)"
```

### Ejecutar Tests
```
F1 → "Run Task" → "Maven: Test"
```

## 📋 Tareas Disponibles en VS Code

1. **Maven: Clean Build** (por defecto con Ctrl+Shift+B)
   - Limpia y compila el proyecto

2. **Maven: Package (Build JAR)**
   - Crea el JAR ejecutable

3. **Run Spring Boot App (Java 21)**
   - Ejecuta la aplicación compilada
   - Depende de: Maven: Package (Build JAR)

4. **Maven: Test**
   - Ejecuta los tests unitarios

## ⚙️ Configuración de VS Code

El proyecto está configurado en `.vscode/` con:

- **settings.json**: 
  - JDK: Java 21 como predeterminado
  - Maven: Configurado con JAVA_HOME
  - Configuraciones de formato de código

- **tasks.json**: 
  - Tareas personalizadas para compilar y ejecutar
  - Integración con Maven y Java 21

## 🔧 Verificar Java 21

```powershell
C:\Users\Gabriel\.jdk\jdk-21.0.8\bin\java -version
```

Deberías ver:
```
java version "21.0.8"
Java(TM) SE Runtime Environment (build 21.0.8+...)
```

## 📝 Notas Importantes

- Java 21 instalado en: `C:\Users\Gabriel\.jdk\jdk-21.0.8`
- Maven instalado en: `C:\Users\Gabriel\.maven\maven-3.9.11`
- Spring Boot actualizado a 3.3.5 (compatible con Java 21)
- Jakarta EE utilizado en lugar de Java EE (javax.*)

## 🐛 Solución de Problemas

### Error: "No se encontró JAVA_HOME"
Asegúrate de ejecutar desde PowerShell con los scripts que configuran JAVA_HOME.

### Error: "JAR no encontrado"
Ejecuta primero: `mvn clean package`

### Error: "Puerto 8080 ya está en uso"
La aplicación intenta usar el puerto 8080. Cambia en `application.properties`:
```properties
server.port=8081
```

## 🎯 Próximos Pasos

1. Configura tu `application.properties` con:
   - Base de datos MySQL
   - Credenciales Firebase (si es necesario)
   - Configuraciones adicionales

2. Ejecuta la aplicación y verifica que inicia correctamente

3. Prueba los endpoints de la API

¡Listo para desarrollar con Java 21! 🎉
