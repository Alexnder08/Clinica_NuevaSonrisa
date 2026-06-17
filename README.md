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


## Configuracion de correo

Para enviar recordatorios y recuperar contrasenas, define estas variables de entorno:

- `SMTP_HOST`
- `SMTP_PORT`
- `SMTP_USER`
- `SMTP_PASS`
- `SMTP_FROM` opcional

Tambien puedes usar `FEATURE_EMAILS=false` para dejar estas funciones preparadas en el codigo pero desactivadas mientras la base de datos no se adapte.

## Comando para ejecutar

En la raíz del proyecto:
- Instalar Java 21

```bash
./mvnw clean javafx:run




