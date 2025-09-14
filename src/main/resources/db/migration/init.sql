CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
                                     id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(320) NOT NULL,
    password_hash   VARCHAR(100) NOT NULL,
    full_name       VARCHAR(255),
    status          VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ,
    last_login_at   TIMESTAMPTZ,
    user_role            VARCHAR(16)  NOT NULL DEFAULT 'USER',
    CONSTRAINT users_status_ck CHECK (status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT users_role_ck   CHECK (role   IN ('USER','ADMIN'))
    );

CREATE UNIQUE INDEX IF NOT EXISTS users_email_unique_idx ON users (lower(email));

CREATE TABLE IF NOT EXISTS refresh_token (
                                             id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash    VARCHAR(128) NOT NULL,           -- SHA-512 hex (128 chars)
    issued_at     TIMESTAMPTZ NOT NULL,
    expires_at    TIMESTAMPTZ NOT NULL,
    rotated_from  UUID,
    revoked_at    TIMESTAMPTZ
    );

-- Un token (hash) non può esistere due volte
CREATE UNIQUE INDEX IF NOT EXISTS refresh_token_hash_uq ON refresh_token (token_hash);

-- Query frequenti: token attivi di un utente
CREATE INDEX IF NOT EXISTS refresh_token_user_active_idx
    ON refresh_token (user_id)
    WHERE revoked_at IS NULL;

-- (opzionale) indice per pulizie scadenze
CREATE INDEX IF NOT EXISTS refresh_token_expires_idx ON refresh_token (expires_at);

-- ======== INSERT =========

-- pippo / pippo123
INSERT INTO public.users (id, email, password_hash, full_name, status, role, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'pippo@example.com',
           '$2a$10$Kh3u3Ec/IXr6AxgY1XZJrO8wlPmtFHTGncu4DEHk0edn7ov4DszVq',
           'Pippo',
           'ACTIVE',
           'USER',
           now(),
           now()
       );

-- pluto / pluto123
INSERT INTO public.users (id, email, password_hash, full_name, status, role, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'pluto@example.com',
           '$2a$10$w9PQb7gv1aLyiE5BejaqCeY5V7i0Hgys8VwzL23t6q4OjwL/fv0Gm',
           'Pluto',
           'ACTIVE',
           'USER',
           now(),
           now()
       );

-- paperino / paperino123
INSERT INTO public.users (id, email, password_hash, full_name, status, role, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'paperino@example.com',
           '$2a$10$3tF8n3Y6ZSk9vHq2X8sT8uYdAKOoU0z6HRTuMfbgIXKp3o71LXomS',
           'Paperino',
           'ACTIVE',
           'USER',
           now(),
           now()
       );