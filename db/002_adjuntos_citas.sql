create table if not exists public.cita_adjuntos (
    id bigserial primary key,
    cita_id integer not null references public.citas(id) on delete cascade,
    usuario_id integer not null references public.usuarios(id),
    nombre_archivo varchar(255) not null,
    tipo_contenido varchar(150),
    tamano_bytes bigint not null check (tamano_bytes between 1 and 10485760),
    contenido bytea not null,
    creado_en timestamp without time zone not null default timezone('America/Lima'::text, now())
);

create index if not exists cita_adjuntos_cita_id_idx
on public.cita_adjuntos (cita_id);

alter table public.cita_adjuntos enable row level security;
revoke all on table public.cita_adjuntos from anon, authenticated;
revoke all on sequence public.cita_adjuntos_id_seq from anon, authenticated;
