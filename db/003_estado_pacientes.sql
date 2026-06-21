alter table public.pacientes
add column if not exists estado varchar(15) not null default 'Activo';

alter table public.pacientes
drop constraint if exists pacientes_estado_check;

alter table public.pacientes
add constraint pacientes_estado_check check (estado in ('Activo', 'Inactivo'));
