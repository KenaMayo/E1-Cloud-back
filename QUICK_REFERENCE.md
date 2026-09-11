# Quick Reference - Tienda de Perritos Architecture

## ✅ Completed Components

### 1. Microservice: ms-productos (Port 8081)
- **Entities**: Producto with full audit fields
- **Endpoints**: GET (public), POST/PUT/DELETE (authenticated)
- **Database**: PostgreSQL table `productos`
- **Security**: JWT Filter + OAuth2 Resource Server
- **Features**: Search by name, validation, exception handling

### 2. Microservice: ms-usuarios (Port 8082)
- **Entities**: Usuario with role management
- **Endpoints**: GET/PUT /me (self), admin endpoints for user management
- **Database**: PostgreSQL table `usuarios`
- **Security**: JWT Filter + OAuth2 Resource Server
- **Features**: Email-based lookup, role-based access control

### 3. BFF - Backend for Frontend (Port 8080)
- **Clients**: WebClient-based reactive HTTP clients
- **Controllers**: Proxy + aggregation endpoints
- **Features**: 
  - `/auth/me` - Current user info
  - `/dashboard` - Aggregated data (user + product count + user count)
  - `/productos/*` - Proxy to ms-productos
  - `/usuarios/*` - Proxy to ms-usuarios
- **Security**: JWT validation + CORS

## 🔐 Security Implementation

### JWT Validation Chain
1. **Frontend** → sends JWT in Authorization header
2. **API Gateway** → validates JWT (issuer, audience, signature)
3. **BFF** → validates JWT again, extracts claims, forwards token
4. **Microservices** → validate JWT, apply role-based authorization

### Authentication Flows
```
PUBLIC GET /productos          → No token needed
Protected POST /productos      → JWT required
Admin PUT /usuarios/{id}       → JWT + ROLE_ADMIN required
User GET /usuarios/me          → JWT + any role
```

## 📊 Database Schema

### Table: productos
```
id (PK), nombre (VARCHAR 100), descripcion (TEXT), precio (DECIMAL), 
stock (INTEGER), created_at, updated_at
```

### Table: usuarios
```
id (PK), email (VARCHAR 100, UNIQUE), nombre (VARCHAR 100), 
apellido (VARCHAR 100), roles (VARCHAR 100), created_at, updated_at
```

## 🚀 Running Locally

```bash
# Terminal 1: PostgreSQL
docker run --name postgres -e POSTGRES_DB=tienda_perritos \
  -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:15

# Terminal 2: ms-productos
cd tienda-perritos-ms-productos && mvn spring-boot:run

# Terminal 3: ms-usuarios
cd tienda-perritos-ms-usuarios && mvn spring-boot:run

# Terminal 4: BFF
cd tienda-perritos-bff && mvn spring-boot:run

# Test (with valid JWT)
curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/v1/dashboard
```

## 📋 File Structure Summary

```
tienda-perritos/
├── ARCHITECTURE.md                    # Design decisions
├── IMPLEMENTACION.md                  # Productos & Usuarios detail
├── JWT_BFF_IMPLEMENTATION.md          # This implementation
│
├── tienda-perritos-ms-productos/
│   ├── pom.xml
│   ├── src/main/java/com/tienda/productos/
│   │   ├── MsProductosApplication.java
│   │   ├── controller/ProductoController.java
│   │   ├── service/ProductoService.java
│   │   ├── repository/ProductoRepository.java
│   │   ├── entity/Producto.java
│   │   ├── dto/{ProductoDto, CreateProductoRequest}.java
│   │   ├── security/JwtAuthenticationFilter.java
│   │   ├── config/{SecurityConfig, GlobalExceptionHandler}.java
│   │   └── exception/ProductoNotFoundException.java
│   └── src/main/resources/application.yml
│
├── tienda-perritos-ms-usuarios/
│   ├── pom.xml
│   ├── src/main/java/com/tienda/usuarios/
│   │   ├── MsUsuariosApplication.java
│   │   ├── controller/UsuarioController.java
│   │   ├── service/UsuarioService.java
│   │   ├── repository/UsuarioRepository.java
│   │   ├── entity/Usuario.java
│   │   ├── dto/{UsuarioDto, CreateUsuarioRequest}.java
│   │   ├── security/JwtAuthenticationFilter.java
│   │   ├── config/{SecurityConfig, GlobalExceptionHandler}.java
│   │   └── exception/UsuarioNotFoundException.java
│   └── src/main/resources/application.yml
│
└── tienda-perritos-bff/
    ├── pom.xml
    ├── src/main/java/com/tienda/bff/
    │   ├── BffApplication.java
    │   ├── config/{SecurityConfig, GlobalExceptionHandler}.java
    │   ├── client/{ProductosClient, UsuariosClient}.java
    │   ├── controller/{AuthController, DashboardController, 
    │   │                ProductosController, UsuariosController}.java
    │   └── dto/{ProductoDto, UsuarioDto, DashboardDto}.java
    └── src/main/resources/application.yml
```

## 🔗 API Routes Summary

### ms-productos: 8081/api/v1
```
GET     /productos              (public)
GET     /productos?nombre=X     (public, search)
GET     /productos/{id}         (public)
POST    /productos              (JWT required)
PUT     /productos/{id}         (JWT required)
DELETE  /productos/{id}         (JWT required)
```

### ms-usuarios: 8082/api/v1
```
GET     /usuarios/me            (JWT required)
PUT     /usuarios/me            (JWT required)
GET     /usuarios               (JWT + ADMIN)
GET     /usuarios/{id}          (JWT + ADMIN)
GET     /usuarios/email/{email} (JWT + ADMIN)
POST    /usuarios               (JWT + ADMIN)
PUT     /usuarios/{id}          (JWT + ADMIN)
DELETE  /usuarios/{id}          (JWT + ADMIN)
```

### BFF: 8080/api/v1
```
GET     /auth/me                (JWT required)
GET     /dashboard              (JWT required)

GET     /productos              (public)
GET     /productos/{id}         (public)
POST    /productos              (JWT required)
PUT     /productos/{id}         (JWT required)
DELETE  /productos/{id}         (JWT required)

GET     /usuarios/me            (JWT required)
PUT     /usuarios/me            (JWT required)
GET     /usuarios               (JWT + ADMIN)
GET     /usuarios/{id}          (JWT + ADMIN)
POST    /usuarios               (JWT + ADMIN)
PUT     /usuarios/{id}          (JWT + ADMIN)
DELETE  /usuarios/{id}          (JWT + ADMIN)
```

## 🔑 Environment Variables

```bash
# Database
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_NAME=tienda_perritos
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres

# OAuth2 / Azure AD (optional, uses defaults if not set)
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://login.microsoftonline.com/common/v2.0
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://login.microsoftonline.com/common/discovery/v2.0/keys

# Microservices URLs (for BFF)
MS_PRODUCTOS_URL=http://localhost:8081/api/v1
MS_USUARIOS_URL=http://localhost:8082/api/v1
```

## ❌ Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Connection refused to PostgreSQL | Start PostgreSQL container first |
| 401 Unauthorized | Ensure JWT token is valid and included in header |
| Microservice not found | Check URL in application.yml, ensure service is running |
| CORS errors | Already configured in SecurityConfig with `*` origins |
| Port already in use | Change port in application.yml or kill process using port |

## 📈 Next Steps

- [ ] Implement Angular + MSAL frontend
- [ ] Create Docker Compose for local stack
- [ ] Setup AWS EC2 + RDS
- [ ] Configure API Gateway with JWT Authorizer
- [ ] Setup CloudFront + S3 for frontend
- [ ] Create GitHub Actions CI/CD pipeline
- [ ] Write unit & integration tests
- [ ] Load testing & performance optimization

---

**Current Status**: 🟢 Core backend complete - Ready for frontend integration
