CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    roles VARCHAR(100) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO usuarios (email, nombre, apellido, roles, created_at, updated_at)
VALUES
    ('a.balbontin.dvt@gmail.com', 'Administrador', 'Tienda', 'ROLE_ADMIN,ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('an.balbontin@duocuc.cl', 'Administrador', 'Tienda', 'ROLE_ADMIN,ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('miikachan001@gmail.com', 'Usuario', 'Tienda', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO UPDATE
SET roles = EXCLUDED.roles,
    updated_at = CURRENT_TIMESTAMP;