# Matriz de requerimientos funcionales

Total: **53 requerimientos**. Las notas `OBSERVADO` corrigen actor o flujo y no se cuentan como requerimientos adicionales.

| # | Requerimiento | Estado | Observacion |
|---:|---|---|---|
| 1 | Iniciar sesion | Cumple | Incluye Administrador, Recepcion y Doctor. |
| 2 | Cerrar sesion | Cumple | Limpia la sesion en los tres dashboards. |
| 3 | Acceder al sistema segun rol | Cumple | Cada actor abre su dashboard autorizado. |
| 4 | Cambiar contrasena | Cumple | Cambio con validaciones. |
| 5 | Recuperacion de contrasena olvidada | Cumple | Codigo temporal por correo con Resend/SMTP. |
| 6 | Historial de accesos | Cumple | Solo esta expuesto en el dashboard Administrador. |
| 7 | Registrar usuario | Cumple | Alta con rol y credenciales. |
| 8 | Consultar usuarios | Cumple | Tabla y busqueda. |
| 9 | Editar usuario | Cumple | Modificacion de datos y rol. |
| 10 | Gestionar estado de usuario | Cumple | Activar o inactivar. |
| 11 | Exportar usuarios en PDF | Cumple | Exporta el listado filtrado. |
| 12 | Visualizar datos completos de usuario | Cumple | Usuario, nombres, DNI, celular, correo, rol y estado. |
| 13 | Exportar usuarios a Excel | Cumple | XLSX con filtros, encabezado fijo y formato. |
| 14 | Asignar servicios al odontologo | Cumple | El Administrador configura los tratamientos que atiende cada Doctor. |
| 15 | Gestionar horarios del odontologo | Cumple | Alta, edicion y consulta de horarios de atencion. |
| 16 | Confirmar reserva por correo | Cumple | Envio HTML mediante Resend al registrar la cita. |
| 17 | Enviar recordatorios de citas | Cumple | Recordatorios de citas proximas desde reportes. |
| 18 | Registrar odontologo | Cumple | Alta de usuario con rol Doctor. |
| 19 | Editar datos del odontologo | Cumple | Edicion de usuario Doctor. |
| 20 | Registrar servicio | Cumple | Alta de tratamiento. |
| 21 | Editar servicio | Cumple | Modificacion de tratamiento. |
| 22 | Eliminar servicio | Cumple | Eliminacion transaccional que protege citas historicas. |
| 23 | Visualizar servicios | Cumple | Catalogo y busqueda. |
| 24 | Visualizar odontologos disponibles | Cumple | Estado, servicio y horarios disponibles. |
| 25 | Registrar paciente | Cumple | Alta con validaciones. |
| 26 | Editar paciente | Cumple | Modificacion de datos. |
| 27 | Inhabilitar paciente | Cumple | Estado Activo/Inactivo; los inactivos conservan historial pero no aparecen al crear citas. |
| 28 | Lista de pacientes | Cumple | Tabla completa. |
| 29 | Busqueda de paciente | Cumple | DNI, nombre y apellido. |
| 30 | Mostrar informacion del paciente | Cumple | DNI, nombres, telefono y correo. |
| 31 | Historial de citas del paciente | Cumple | Vista dedicada desde el paciente con fecha, hora, Doctor, tratamiento, estado y motivo. |
| 32 | Registrar cita | Cumple | Valida horario, doctor y cruces. |
| 33 | Registrar motivo de cita | Cumple | Motivo obligatorio. |
| 34 | Reprogramar cita | Cumple | Revalida disponibilidad. |
| 35 | Cambiar estado de cita | Cumple | Pendiente, En espera y Realizado. |
| 36 | Cancelar cita | Cumple | Motivo obligatorio. |
| 37 | Seleccionar fechas desde calendario | Cumple | DatePicker en formularios y filtros; actores concretos. |
| 38 | Filtrar por tipo de tratamiento | Cumple | Filtro combinable. |
| 39 | Filtrar por fechas | Cumple | Rango desde/hasta. |
| 40 | Filtrar por doctor | Cumple | Filtro combinable. |
| 41 | Visualizar citas en calendario | Cumple | Vista mensual con horas y colores por estado de cita. |
| 42 | Visualizar agenda por odontologo | Cumple | Filtro y horarios por odontologo. |
| 43 | Mostrar pacientes en espera | Cumple | Lista de citas de hoy en estado En espera. |
| 44 | Ver citas pendientes del dia | Cumple | Acceso rapido y dashboard Doctor. |
| 45 | Confirmar asistencia | Cumple | Recepcion cambia Pendiente a En espera y audita. |
| 46 | Visualizar agenda propia del Doctor | Cumple | Mis citas y resumen diario. |
| 47 | Registrar notas en citas | Cumple | Notas clinicas restringidas al Doctor asignado. |
| 48 | Adjuntar archivos a citas | Cumple | PDF, imagenes y Word hasta 10 MB almacenados en PostgreSQL y restringidos al Doctor asignado. |
| 49 | Exportar citas en PDF | Cumple | Exporta la vista filtrada; render verificado. |
| 50 | Imprimir citas | Cumple | Dialogo nativo de impresion JavaFX. |
| 51 | Generar reportes en PDF | Cumple | Resumen por odontologo, tratamiento y estado. |
| 52 | Copias de seguridad | Cumple | El Administrador genera respaldos nativos PostgreSQL con `pg_dump`. |
| 53 | Restaurar copia de seguridad | Cumple | Restauracion con `pg_restore`, seleccion de archivo y confirmacion escrita. |

## Resultado actual

- Cumplidos: **53**
- Parciales: **0**
- Pendientes: **0**
- Total: **53**

## Observaciones corregidas

- RF01: Doctor incluido como actor de inicio de sesion.
- RF08: historial de accesos visible solo para Administrador.
- RF35: seleccion de fechas asociada a Administrador/Recepcion en gestion y al Doctor en su agenda.
- RF33/RF44: flujo completado como `Pendiente -> En espera -> Realizado`; el Doctor solo finaliza con asistencia confirmada y nota clinica.
