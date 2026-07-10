# Sistema Clínica Nueva Sonrisa

Sistema de gestión odontológica desarrollado en JavaFX, Maven y PostgreSQL/Supabase.

## Funcionalidades principales

- Inicio de sesión por roles: Administrador, Recepción y Doctor.
- Gestión de usuarios.
- Gestión de pacientes.
- Gestión de servicios odontológicos.
- Gestión de odontólogos.
- Asignación de servicios por doctor.
- Gestión de horarios de odontólogos.
- Registro, edición y cancelación de citas.
- Validación de cruces de horario.
- Dashboard para Administrador, Recepción y Doctor.
- Historial de accesos.
- Auditoría de acciones.
- Reportes administrativos.
- Exportación de citas y reportes a Excel.
- Recordatorios de citas por correo.
- Recuperación de contraseñas con código temporal por correo.

## Tecnologías utilizadas

- Java 21
- JavaFX 21
- Maven
- PostgreSQL alojado en Supabase
- Supabase
- Apache POI
- Jakarta Mail
- Ikonli FontAwesome
- BCrypt

USERS:

ADMIN

usuario: admin

contraseña: 123

DOCTOR

usuario: doctor1

contraseña: 123

RECEPCIONISTA

usuario: recepcion1

contraseña: 123


## Configuracion de correo con Resend

Para enviar confirmaciones de reserva, recordatorios y recuperaciones de contrasena, define:

- `RESEND_API_KEY` clave privada creada en Resend.
- `RESEND_FROM` remitente verificado, por ejemplo `Nueva Sonrisa <citas@tudominio.com>`.

Cuando `RESEND_API_KEY` esta definida, el correo queda habilitado automaticamente. Para desactivarlo de forma explicita usa `FEATURE_EMAILS=false`.

Durante pruebas se usa `Nueva Sonrisa <onboarding@resend.dev>` si no se define `RESEND_FROM`. Para produccion debes verificar el dominio en Resend.

La configuracion SMTP anterior sigue disponible como respaldo:

- `SMTP_HOST`
- `SMTP_PORT`
- `SMTP_USER`
- `SMTP_PASS`
- `SMTP_FROM` opcional

Para habilitar SMTP sin Resend define tambien `FEATURE_EMAILS=true`.

## Copias de seguridad

El Administrador puede crear y restaurar respaldos PostgreSQL en formato `.backup`. La aplicacion busca PostgreSQL 18 en Windows; para otra instalacion define `PG_BIN_DIR` con la carpeta que contiene `pg_dump.exe` y `pg_restore.exe`.

La conexion puede configurarse con `DATABASE_URL`, `DATABASE_USER` y `DATABASE_PASSWORD`.

Antes de usar adjuntos ejecuta `db/002_adjuntos_citas.sql` en el proyecto Supabase.
Para reforzar estados y transiciones de citas ejecuta tambien `db/003_validaciones_citas.sql`.

## Comando para ejecutar

En la raíz del proyecto:
- Instalar Java 21

```bash
./mvnw clean javafx:run




