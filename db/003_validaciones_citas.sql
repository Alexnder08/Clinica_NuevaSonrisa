-- Valida estados y transiciones de citas desde la base de datos.
-- Ejecutar despues de las migraciones previas del proyecto.

UPDATE citas
SET estado = U&'No asisti\00F3'
WHERE lower(estado) LIKE 'no asist%';

ALTER TABLE citas
DROP CONSTRAINT IF EXISTS estado_cita_chk;

ALTER TABLE citas
DROP CONSTRAINT IF EXISTS chk_citas_estado_valido;

ALTER TABLE citas
ADD CONSTRAINT chk_citas_estado_valido
CHECK (estado IN ('Pendiente', 'En espera', 'Realizado', 'Cancelado', 'No asistio', U&'No asisti\00F3'));

ALTER TABLE citas
DROP CONSTRAINT IF EXISTS uq_doctor_horario;

ALTER TABLE citas
DROP CONSTRAINT IF EXISTS uq_paciente_horario;

DROP INDEX IF EXISTS uq_doctor_horario_activo;
DROP INDEX IF EXISTS uq_paciente_horario_activo;

CREATE UNIQUE INDEX uq_doctor_horario_activo
ON citas (doctor_id, fecha, hora)
WHERE estado IN ('Pendiente', 'En espera', 'Realizado');

CREATE UNIQUE INDEX uq_paciente_horario_activo
ON citas (paciente_id, fecha, hora)
WHERE estado IN ('Pendiente', 'En espera', 'Realizado');

CREATE OR REPLACE FUNCTION validar_transicion_estado_cita()
RETURNS trigger
LANGUAGE plpgsql
SET search_path = public, pg_temp
AS $$
BEGIN
    IF NEW.estado = OLD.estado THEN
        RETURN NEW;
    END IF;

    IF OLD.estado = 'Pendiente' AND NEW.estado = 'En espera' THEN
        IF OLD.fecha <> CURRENT_DATE THEN
            RAISE EXCEPTION 'Solo se puede pasar a En espera una cita pendiente del dia de hoy.';
        END IF;
        RETURN NEW;
    END IF;

    IF OLD.estado = 'En espera' AND NEW.estado = 'Realizado' THEN
        IF btrim(COALESCE(NEW.notas, '')) = '' THEN
            RAISE EXCEPTION 'Registre una nota clinica antes de finalizar la cita.';
        END IF;
        RETURN NEW;
    END IF;

    IF OLD.estado IN ('Pendiente', 'En espera') AND NEW.estado = 'Cancelado' THEN
        IF btrim(COALESCE(NEW.motivo_cancelacion, '')) = '' THEN
            RAISE EXCEPTION 'El motivo de cancelacion es obligatorio.';
        END IF;
        RETURN NEW;
    END IF;

    IF OLD.estado = 'Pendiente' AND lower(NEW.estado) LIKE 'no asist%' THEN
        RETURN NEW;
    END IF;

    RAISE EXCEPTION 'No se permite cambiar el estado de % a %.', OLD.estado, NEW.estado;
END;
$$;

DROP TRIGGER IF EXISTS trg_validar_transicion_estado_cita ON citas;

CREATE TRIGGER trg_validar_transicion_estado_cita
BEFORE UPDATE OF estado ON citas
FOR EACH ROW
EXECUTE FUNCTION validar_transicion_estado_cita();
