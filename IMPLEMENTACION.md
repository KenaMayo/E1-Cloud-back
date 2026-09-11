# Implementación de Microservicios - Tienda de Perritos

## Estado de Implementación

### ✅ ms-productos (Microservicio de Productos)

#### Estructura Completada:
```
tienda-perritos-ms-productos/
├── src/main/java/com/tienda/productos/
│   ├── MsProductosApplication.java         ✅ (Main Application)
│   ├── controller/
│   │   └── ProductoController.java         ✅ (CRUD endpoints)
│   ├── service/
│   │   └── ProductoService.java            ✅ (Business logic)
│   ├── repository/
│   │   └── ProductoRepository.java         ✅ (JPA Repository)
│   ├── entity/
│   │   └── Producto.java                   ✅ (JPA Entity)
│   ├── dto/
│   │   ├── ProductoDto.java                ✅ (Response DTO)
│   │   └── CreateProductoRequest.java      ✅ (Request DTO)
│   ├── exception/
│   │   └── ProductoNotFoundException.java  ✅ (Custom exception)
│   └── config/
│       ├── GlobalExceptionHandler.java     ✅ (Exception handling)
│       └── SecurityConfig.java             ✅ (OAuth2 + JWT security)
└── pom.xml                                 ✅ (Dependencies configured)
```

#### Endpoints Implementados:

| Método | Ruta | Autenticación | Descripción |
|--------|------|---------------|------------|
| GET | `/api/v1/productos` | ❌ Pública | Lista todos los productos |
| GET | `/api/v1/productos?nombre=...` | ❌ Pública | Busca productos por nombre |
| GET | `/api/v1/productos/{id}` | ❌ Pública | Obtiene un producto por ID |
| POST | `/api/v1/productos` | ✅ JWT + ADMIN | Crea un nuevo producto |
| PUT | `/api/v1/productos/{id}` | ✅ JWT + ADMIN | Actualiza un producto |
| DELETE | `/api/v1/productos/{id}` | ✅ JWT + ADMIN | Elimina un producto |

#### Características:
- ✅ JPA/Hibernate con PostgreSQL
- ✅ Validación de entrada (javax.validation)
- ✅ Manejo de excepciones global
- ✅ Logging estructurado
- ✅ Configuración OAuth2 Resource Server
- ✅ CORS habilitado
- ✅ OpenAPI/Swagger integrado
- ✅ Actuator endpoints para monitoreo

#### Propiedades de Producto:
- `id` (Long): ID único
- `nombre` (String, requerido): Nombre del producto (máx 100 chars)
- `descripcion` (String): Descripción detallada
- `precio` (BigDecimal, requerido): Precio > 0
- `stock` (Integer, requerido): Cantidad en stock (>= 0)
- `createdAt` (LocalDateTime): Timestamp de creación
- `updatedAt` (LocalDateTime): Timestamp de última actualización

---

### ✅ ms-usuarios (Microservicio de Usuarios)

#### Estructura Completada:
```
tienda-perritos-ms-usuarios/
├── src/main/java/com/tienda/usuarios/
│   ├── MsUsuariosApplication.java          ✅ (Main Application)
│   ├── controller/
│   │   └── UsuarioController.java          ✅ (CRUD + /me endpoints)
│   ├── service/
│   │   └── UsuarioService.java             ✅ (Business logic)
│   ├── repository/
│   │   └── UsuarioRepository.java          ✅ (JPA Repository)
│   ├── entity/
│   │   └── Usuario.java                    ✅ (JPA Entity)
│   ├── dto/
│   │   ├── UsuarioDto.java                 ✅ (Response DTO)
│   │   └── CreateUsuarioRequest.java       ✅ (Request DTO)
│   ├── exception/
│   │   └── UsuarioNotFoundException.java   ✅ (Custom exception)
│   └── config/
│       ├── GlobalExceptionHandler.java     ✅ (Exception handling)
│       └── SecurityConfig.java             ✅ (OAuth2 + JWT security)
└── pom.xml                                 ✅ (Dependencies configured)
```

#### Endpoints Implementados:

| Método | Ruta | Autenticación | Descripción |
|--------|------|---------------|------------|
| GET | `/api/v1/usuarios/me` | ✅ JWT | Obtiene usuario actual |
| PUT | `/api/v1/usuarios/me` | ✅ JWT | Actualiza perfil propio |
| GET | `/api/v1/usuarios` | ✅ JWT + ADMIN | Lista todos los usuarios |
| GET | `/api/v1/usuarios/email/{email}` | ✅ JWT + ADMIN | Obtiene usuario por email |
| GET | `/api/v1/usuarios/{id}` | ✅ JWT + ADMIN | Obtiene usuario por ID |
| POST | `/api/v1/usuarios` | ✅ JWT + ADMIN | Crea un nuevo usuario |
| PUT | `/api/v1/usuarios/{id}` | ✅ JWT + ADMIN | Actualiza un usuario |
| DELETE | `/api/v1/usuarios/{id}` | ✅ JWT + ADMIN | Elimina un usuario |

#### Características:
- ✅ JPA/Hibernate con PostgreSQL
- ✅ Validación de entrada (javax.validation)
- ✅ Endpoint `/me` para usuario actual
- ✅ Búsqueda por email
- ✅ Gestión de roles (CSV format)
- ✅ Manejo de excepciones global
- ✅ Logging estructurado
- ✅ Configuración OAuth2 Resource Server
- ✅ CORS habilitado
- ✅ OpenAPI/Swagger integrado
- ✅ Actuator endpoints para monitoreo

#### Propiedades de Usuario:
- `id` (Long): ID único
- `email` (String, requerido, único): Email del usuario
- `nombre` (String, requerido): Nombre (máx 100 chars)
- `apellido` (String, requerido): Apellido (máx 100 chars)
- `roles` (String): Roles CSV (ej: "ROLE_USER,ROLE_ADMIN")
- `createdAt` (LocalDateTime): Timestamp de creación
- `updatedAt` (LocalDateTime): Timestamp de última actualización

---

## Configuración de Seguridad

### OAuth2 Resource Server (JWT)

Ambos microservicios están configurados para validar JWT tokens desde Azure AD:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI:https://login.microsoftonline.com/common/v2.0}
          jwk-set-uri: ${SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI:https://login.microsoftonline.com/common/discovery/v2.0/keys}
```

### Configuración de Puertos

- **ms-productos**: Puerto 8081
- **ms-usuarios**: Puerto 8082
- **Context Path**: `/api/v1` (para ambas)

### CORS Configuration

- Orígenes permitidos: `*` (configurable)
- Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS
- Headers permitidos: `*`
- Max age: 3600 segundos

---

## Base de Datos

### Tablas Creadas Automáticamente:

#### Tabla: productos
```sql
CREATE TABLE productos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(19,2) NOT NULL,
    stock INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### Tabla: usuarios
```sql
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    roles VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Variables de Entorno:
- `DATABASE_HOST`: Host de PostgreSQL (default: localhost)
- `DATABASE_PORT`: Puerto de PostgreSQL (default: 5432)
- `DATABASE_NAME`: Nombre de la BD (default: tienda_perritos)
- `DATABASE_USER`: Usuario de BD (default: postgres)
- `DATABASE_PASSWORD`: Contraseña de BD (default: postgres)

---

## Validaciones Implementadas

### Producto (CreateProductoRequest):
- ✅ `nombre`: Requerido, no en blanco
- ✅ `precio`: Requerido, mínimo 0.01
- ✅ `stock`: Requerido, mínimo 0

### Usuario (CreateUsuarioRequest):
- ✅ `email`: Requerido, debe ser email válido
- ✅ `nombre`: Requerido, no en blanco
- ✅ `apellido`: Requerido, no en blanco
- ✅ `roles`: Opcional (default: ROLE_USER)

---

## Manejo de Errores

### Respuesta de Error Estándar:
```json
{
    "timestamp": "2026-09-09T20:45:30.123456",
    "status": 404,
    "error": "Not Found",
    "message": "Producto con ID 999 no encontrado",
    "path": "/api/v1/productos/999"
}
```

### Códigos de Error:
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado
- `204 No Content`: Eliminación exitosa
- `400 Bad Request`: Validación fallida
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error no manejado

---

## Próximos Pasos

1. **BFF (Backend for Frontend)** - Orquestador central
   - Agregar endpoints de agregación
   - Implementar rutas a microservicios
   - Validación JWT centralizada

2. **Frontend Angular** - Con MSAL
   - Autenticación con Azure AD
   - Integración con endpoints

3. **Docker Compose** - Local development
   - Configurar todos los servicios
   - Network compartida

4. **Deployment** - AWS
   - EC2 + Docker Compose
   - RDS PostgreSQL
   - API Gateway con JWT Authorizer
   - CloudFront + S3 para frontend

---

## Testing Recomendado

### Unitarios:
```bash
mvn test
```

### Integración (con TestContainers + PostgreSQL):
```bash
mvn verify
```

### Manual (Local):
```bash
# Terminal 1: ms-productos
cd tienda-perritos-ms-productos
mvn spring-boot:run

# Terminal 2: ms-usuarios
cd tienda-perritos-ms-usuarios
mvn spring-boot:run

# Terminal 3: Pruebas
curl http://localhost:8081/api/v1/productos
curl http://localhost:8082/api/v1/usuarios
```

---

**Estado**: ✅ COMPLETADO - Listos para integración con BFF
