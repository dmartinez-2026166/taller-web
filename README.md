# Taller web – Serviautos Mardel

Versión web del sistema del taller electromecánico (Spring Boot 3 + Thymeleaf + MySQL).
Registra el cliente, su vehículo y el trabajo a realizar en una sola pantalla.

## Requisitos
- JDK 21
- Maven 3.9+ (o abrir el proyecto en IntelliJ / VS Code con la extensión de Spring Boot)
- MySQL 8

## Puesta en marcha

1. **Base de datos**: ejecute `sql/DDL.sql` en MySQL Workbench.
   Se puede ejecutar varias veces; no borra datos. Si ya usaba la app de escritorio,
   la misma base `sistema_taller_electromecanico` sirve tal cual.
2. **Configuración**: abra `src/main/resources/application.properties` y defina:
   - la contraseña de MySQL (`spring.datasource.password`, o la variable de entorno `DB_PASSWORD`);
   - nombre, usuario y contraseña del administrador y del empleado.
   Una contraseña que empiece con `CAMBIAR` no crea el usuario.
3. **Iniciar**:
   ```
   mvn spring-boot:run
   ```
4. Abrir <http://localhost:8080> e iniciar sesión.

Al iniciar, la aplicación crea los dos usuarios base con la contraseña cifrada con BCrypt.
Si ya existen usuarios de la app de escritorio (contraseña MD5) con el mismo nombre de usuario,
su contraseña se migra a BCrypt con la que usted configure.

## Roles
- **ADMINISTRADOR**: agregar, editar y eliminar trabajos.
- **EMPLEADO**: agregar y editar (no puede eliminar).

## Usarlo desde otros equipos del taller
Con la aplicación corriendo en una PC, desde las demás se abre `http://IP-DE-ESA-PC:8080`
(permita el puerto 8080 en el firewall de Windows).

## Publicarlo en internet
```
mvn clean package
java -jar target/taller-web-1.0.0.jar
```
- Use variables de entorno para los secretos (`DB_USER`, `DB_PASSWORD`, `ADMIN_PASSWORD`, `EMPLEADO_PASSWORD`...).
- Ponga HTTPS delante con un proxy inverso (Caddy o Nginx con Let's Encrypt).
- No exponga el puerto 3306 de MySQL a internet.
