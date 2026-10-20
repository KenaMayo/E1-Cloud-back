-- Login basado en base de datos (reemplazo temporal de MSAL).
-- Dependencias revisadas: ninguna tabla referencia a "usuarios" mediante clave foranea
-- (solo "productos" existe y es independiente), por lo que el DROP ... CASCADE no rompe nada.
-- Las contrasenas se guardan unicamente como hash BCrypt (costo 10).

DROP TABLE IF EXISTS contrasenas CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'USER')),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL DEFAULT now(),
    actualizado_en TIMESTAMP DEFAULT now()
);

CREATE TABLE contrasenas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuarios (id) ON DELETE CASCADE,
    hash_contrasena VARCHAR(100) NOT NULL,
    actualizado_en TIMESTAMP DEFAULT now()
);

INSERT INTO usuarios (username, email, nombre, apellido, rol) VALUES
    ('admin', 'admin@tienda.local', 'Administrador', 'Tienda', 'ADMIN'),
    ('user',  'user@tienda.local',  'Usuario',        'Tienda', 'USER');

INSERT INTO contrasenas (usuario_id, hash_contrasena)
SELECT id, '$2a$10$D48N96qvcHVGM7uf4UIpi.ZPAxux5Hzthe6h5vjZvnd5pneXR0FnS' FROM usuarios WHERE username = 'admin';

INSERT INTO contrasenas (usuario_id, hash_contrasena)
SELECT id, '$2a$10$OG6W706jLYbEYF/W6DNeEOuhgMmTPK1Z1G0bdDTeH4U.PqiBJ5VEC' FROM usuarios WHERE username = 'user';
