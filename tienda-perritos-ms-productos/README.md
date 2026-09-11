# ms-productos — Microservicio de Productos

Microservicio Spring Boot 3.3 para gestión de CRUD de productos.

## Características

- ✅ CRUD de productos (Create, Read, Update, Delete)
- ✅ Validación de entrada con `@Valid`
- ✅ Exception handling global con `@ControllerAdvice`
- ✅ JPA + Spring Data Repository
- ✅ PostgreSQL
- ✅ Logging con SLF4J

## Requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+

## Compilación

```bash
mvn clean package
```

## Ejecución Local

### Opción 1: Maven
```bash
mvn spring-boot:run
```

### Opción 2: JAR
```bash
java -jar target/ms-productos-1.0.0.jar
```

### Opción 3: Docker Compose (recomendado)
Desde la raíz del proyecto:
```bash
docker-compose up
```

El servicio estará disponible en: `http://localhost:8081/api/v1`

## Variables de Entorno

```properties
DATABASE_HOST=localhost        # Host de PostgreSQL
DATABASE_PORT=5432            # Puerto
DATABASE_NAME=tienda_perritos # Nombre de BD
DATABASE_USER=postgres        # Usuario
DATABASE_PASSWORD=postgres    # Contraseña
```

## Endpoints

### Listar todos los productos
```bash
GET /api/v1/productos
```

Parámetro opcional:
- `?nombre=<búsqueda>` - Busca por nombre (case-insensitive)

### Obtener producto por ID
```bash
GET /api/v1/productos/{id}
```

### Crear producto
```bash
POST /api/v1/productos
Content-Type: application/json

{
  "nombre": "Alimento Premium para Perros",
  "descripcion": "Alimento premium con proteínas naturales",
  "precio": 45.99,
  "stock": 100
}
```

### Actualizar producto
```bash
PUT /api/v1/productos/{id}
Content-Type: application/json

{
  "nombre": "Alimento Premium para Perros",
  "descripcion": "Alimento premium actualizado",
  "precio": 49.99,
  "stock": 150
}
```

### Eliminar producto
```bash
DELETE /api/v1/productos/{id}
```

## Tests

```bash
mvn test
```

## Próximas Fases

- [ ] Agregar JWT Filter (Spring Security + OAuth2)
- [ ] Agregar autorización por roles
- [ ] Migración DB con Flyway
- [ ] OpenAPI/Swagger docs

---

**Estado**: Fase 2 sin seguridad ✓  
**Próximo**: Agregar JWT Filter y autorización
