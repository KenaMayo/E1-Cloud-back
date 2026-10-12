DROP TABLE IF EXISTS productos;

CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    descripcion VARCHAR(255),
    nombre VARCHAR(255) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    stock INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO productos (
    created_at,
    descripcion,
    nombre,
    precio,
    stock,
    updated_at
)
VALUES
(
    CURRENT_TIMESTAMP,
    'Alimento completo para perros adultos.',
    'Alimento Premium Adulto',
    24990,
    25,
    CURRENT_TIMESTAMP
),
(
    CURRENT_TIMESTAMP,
    'Alimento para cachorros de razas pequeñas.',
    'Alimento Premium Cachorro',
    18990,
    30,
    CURRENT_TIMESTAMP
),
(
    CURRENT_TIMESTAMP,
    'Juguete resistente para perros.',
    'Pelota Mordedora',
    5990,
    15,
    CURRENT_TIMESTAMP
),
(
    CURRENT_TIMESTAMP,
    'Cama acolchada para perros pequeños.',
    'Cama para Perrito',
    29990,
    10,
    CURRENT_TIMESTAMP
),
(
    CURRENT_TIMESTAMP,
    'Correa ajustable para paseos.',
    'Correa Ajustable',
    8990,
    20,
    CURRENT_TIMESTAMP
);
