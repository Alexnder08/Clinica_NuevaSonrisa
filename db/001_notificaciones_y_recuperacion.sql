alter table public.usuarios
add column if not exists email varchar(255);

create unique index if not exists usuarios_email_key
on public.usuarios (email)
where email is not null and email <> '';

alter table public.citas
add column if not exists recordatorio_enviado_at timestamp without time zone;

create table if not exists public.password_reset_tokens (
    id bigserial primary key,
    usuario_id integer not null references public.usuarios(id) on delete cascade,
    token_hash varchar(255) not null,
    expires_at timestamp without time zone not null,
    used_at timestamp without time zone,
    created_at timestamp without time zone not null default timezone('America/Lima'::text, now())
);

create index if not exists password_reset_tokens_usuario_id_idx
on public.password_reset_tokens (usuario_id);

create index if not exists password_reset_tokens_expires_at_idx
on public.password_reset_tokens (expires_at);
