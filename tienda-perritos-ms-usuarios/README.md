# ms-usuarios — Microservicio de Usuarios

Microservicio Spring Boot 3.3 para gestión de usuarios y autenticación.

## Características

- ✅ CRUD de usuarios (Create, Read, Update, Delete)
- ✅ Búsqueda por email
- ✅ Gestión de roles
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
java -jar target/ms-usuarios-1.0.0.jar
```

### Opción 3: Docker Compose (recomendado)
Desde la raíz del proyecto:
```bash
docker-compose up
```

El servicio estará disponible en: `http://localhost:8082/api/v1`

## Variables de Entorno

```properties
DATABASE_HOST=localhost        # Host de PostgreSQL
DATABASE_PORT=5432            # Puerto
DATABASE_NAME=tienda_perritos # Nombre de BD
DATABASE_USER=postgres        # Usuario
DATABASE_PASSWORD=postgres    # Contraseña
```

## Endpoints

### Listar todos los usuarios
```bash
GET /api/v1/usuarios
```

### Obtener usuario por ID
```bash
GET /api/v1/usuarios/{id}
```

### Obtener usuario por email
```bash
GET /api/v1/usuarios/email/{email}
```

### Crear usuario
```bash
POST /api/v1/usuarios
Content-Type: application/json

{
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "roles": "ROLE_USER,ROLE_ADMIN"
}
```

### Actualizar usuario
```bash
PUT /api/v1/usuarios/{id}
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "roles": "ROLE_USER"
}
```

### Eliminar usuario
```bash
DELETE /api/v1/usuarios/{id}
```

## Roles Soportados

- `ROLE_USER`: Usuario estándar
- `ROLE_ADMIN`: Administrador del sistema
- `ROLE_GUEST`: Invitado (solo lectura)

## Tests

```bash
mvn test
```

## Próximas Fases

- [ ] Agregar JWT Filter (Spring Security + OAuth2)
- [ ] Endpoint `/api/v1/usuarios/me` para obtener usuario autenticado
- [ ] Validación de Azure AD claims

---

**Estado**: Fase 2 sin seguridad ✓  
**Próximo**: Agregar JWT Filter y autorización
