# JWT Filters & BFF (Backend for Frontend) Implementation

## 📋 Resumen de Implementación

Se ha completado la implementación de:
1. **JWT Filters** - Validación de tokens JWT en ambos microservicios
2. **BFF (Backend for Frontend)** - Orquestador central con aggregación de datos

---

## 🔐 JWT Filters - Validación de Tokens

### Ubicación de los Filters

#### ms-productos
```
src/main/java/com/tienda/productos/security/JwtAuthenticationFilter.java
```

#### ms-usuarios
```
src/main/java/com/tienda/usuarios/security/JwtAuthenticationFilter.java
```

### Funcionalidad del Filter

```java
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) 
    throws ServletException, IOException {
        
        // Extrae el JWT del contexto de seguridad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            log.debug("JWT Token validado para usuario: {}", auth.getName());
            log.debug("Authorities: {}", auth.getAuthorities());
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Características del Filter

- ✅ **Extrae JWT**: Obtiene el token del header Authorization
- ✅ **Valida issuer**: Verifica que el token sea de Azure AD
- ✅ **Valida firma**: Usa JWKS endpoint para validar
- ✅ **Extrae claims**: Obtiene usuario y roles del JWT
- ✅ **Logging**: Registra auditoría de validaciones
- ✅ **Defense in Depth**: Complementa validación de API Gateway

### Configuración de Security

Ambos microservicios se actualizaron para registrar el JWT Filter:

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/productos").permitAll()
                .requestMatchers(HttpMethod.POST, "/productos").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(null)))
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 🔄 BFF (Backend for Frontend) - Orquestador Central

### Estructura del BFF

```
tienda-perritos-bff/
├── pom.xml                                ✅
├── src/main/
│   ├── java/com/tienda/bff/
│   │   ├── BffApplication.java           ✅ (Main app)
│   │   ├── config/
│   │   │   ├── SecurityConfig.java       ✅ (OAuth2 + JWT)
│   │   │   └── GlobalExceptionHandler.java ✅
│   │   ├── client/
│   │   │   ├── ProductosClient.java      ✅ (HTTP calls to ms-productos)
│   │   │   └── UsuariosClient.java       ✅ (HTTP calls to ms-usuarios)
│   │   ├── controller/
│   │   │   ├── AuthController.java       ✅ (/auth/me endpoint)
│   │   │   ├── DashboardController.java  ✅ (/dashboard aggregation)
│   │   │   ├── ProductosController.java  ✅ (proxy endpoints)
│   │   │   └── UsuariosController.java   ✅ (proxy endpoints)
│   │   └── dto/
│   │       ├── ProductoDto.java          ✅
│   │       ├── UsuarioDto.java           ✅
│   │       └── DashboardDto.java         ✅
│   └── resources/
│       └── application.yml               ✅
└── .gitignore                            ✅
```

### Configuración de Puerto

- **BFF Puerto**: 8080
- **Context Path**: `/api/v1`
- **URL interna ms-productos**: `http://localhost:8081/api/v1`
- **URL interna ms-usuarios**: `http://localhost:8082/api/v1`

### Configuración de Environment Variables

```yaml
microservices:
  productos:
    url: ${MS_PRODUCTOS_URL:http://localhost:8081/api/v1}
  usuarios:
    url: ${MS_USUARIOS_URL:http://localhost:8082/api/v1}
```

---

## 📡 Clientes HTTP (WebClient - Reactive)

### ProductosClient

```java
@Service
@RequiredArgsConstructor
public class ProductosClient {
    
    private final WebClient webClient;
    
    // Obtiene todos los productos
    public Flux<ProductoDto> obtenerTodosProductos(String token)
    
    // Obtiene un producto por ID
    public Mono<ProductoDto> obtenerProducto(Long id, String token)
    
    // Crea un nuevo producto
    public Mono<ProductoDto> crearProducto(ProductoDto producto, String token)
    
    // Actualiza un producto
    public Mono<ProductoDto> actualizarProducto(Long id, ProductoDto producto, String token)
    
    // Elimina un producto
    public Mono<Void> eliminarProducto(Long id, String token)
}
```

### UsuariosClient

```java
@Service
@RequiredArgsConstructor
public class UsuariosClient {
    
    private final WebClient webClient;
    
    // Obtiene usuario actual (desde JWT)
    public Mono<UsuarioDto> obtenerUsuarioActual(String token)
    
    // Obtiene todos los usuarios (admin)
    public Flux<UsuarioDto> obtenerTodosUsuarios(String token)
    
    // Obtiene usuario por ID (admin)
    public Mono<UsuarioDto> obtenerUsuario(Long id, String token)
    
    // Crea nuevo usuario (admin)
    public Mono<UsuarioDto> crearUsuario(UsuarioDto usuario, String token)
    
    // Actualiza usuario actual
    public Mono<UsuarioDto> actualizarUsuarioActual(UsuarioDto usuario, String token)
    
    // Actualiza usuario (admin)
    public Mono<UsuarioDto> actualizarUsuario(Long id, UsuarioDto usuario, String token)
    
    // Elimina usuario (admin)
    public Mono<Void> eliminarUsuario(Long id, String token)
}
```

---

## 🎯 Endpoints del BFF

### AuthController (`/api/v1/auth`)

| Método | Ruta | Autenticación | Descripción |
|--------|------|---------------|------------|
| GET | `/auth/me` | ✅ JWT | Retorna info del usuario actual |

**Respuesta**:
```json
{
    "id": 1,
    "email": "usuario@example.com",
    "nombre": "Juan",
    "apellido": "Pérez",
    "roles": "ROLE_USER"
}
```

### DashboardController (`/api/v1/dashboard`)

| Método | Ruta | Autenticación | Descripción |
|--------|------|---------------|------------|
| GET | `/dashboard` | ✅ JWT | Agregación de datos para dashboard |

**Respuesta**:
```json
{
    "usuario": {
        "id": 1,
        "email": "usuario@example.com",
        "nombre": "Juan",
        "apellido": "Pérez",
        "roles": "ROLE_USER"
    },
    "totalProductos": 42,
    "totalUsuarios": 15
}
```

**Lógica de Agregación**:
1. Obtiene usuario actual del ms-usuarios
2. Cuenta total de productos desde ms-productos
3. Cuenta total de usuarios desde ms-usuarios
4. Combina todo con Mono.zip() (programación reactiva)

### ProductosController (`/api/v1/productos`)

Proxy directo a ms-productos:

| Método | Ruta | Descripción |
|--------|------|------------|
| GET | `/productos` | Lista todos los productos |
| GET | `/productos/{id}` | Obtiene producto por ID |
| POST | `/productos` | Crea nuevo producto (ADMIN) |
| PUT | `/productos/{id}` | Actualiza producto (ADMIN) |
| DELETE | `/productos/{id}` | Elimina producto (ADMIN) |

### UsuariosController (`/api/v1/usuarios`)

Proxy directo a ms-usuarios:

| Método | Ruta | Descripción |
|--------|------|------------|
| GET | `/usuarios/me` | Usuario actual |
| PUT | `/usuarios/me` | Actualiza perfil propio |
| GET | `/usuarios` | Lista usuarios (ADMIN) |
| GET | `/usuarios/{id}` | Obtiene usuario (ADMIN) |
| POST | `/usuarios` | Crea usuario (ADMIN) |
| PUT | `/usuarios/{id}` | Actualiza usuario (ADMIN) |
| DELETE | `/usuarios/{id}` | Elimina usuario (ADMIN) |

---

## 🔌 Flujo de Autenticación & Autorización

```
┌─────────────────────────────────────────────┐
│ Frontend (Angular + MSAL)                   │
│ Usuario login → Azure AD → JWT token        │
└────────────────┬────────────────────────────┘
                 │ Authorization: Bearer <JWT>
                 v
        ┌────────────────────┐
        │ AWS API Gateway    │
        │ JWT Authorizer     │
        └────────────┬───────┘
                     │
                     v
        ┌────────────────────────────┐
        │ BFF (Port 8080)            │
        │ - JWT Filter               │
        │ - Extrae claims            │
        │ - Forward token a MS       │
        └────────────┬───────────────┘
                     │
        ┌────────────┴───────────────┐
        │                            │
        v                            v
  ┌──────────────┐          ┌──────────────┐
  │ ms-productos │          │ ms-usuarios  │
  │ (Port 8081)  │          │ (Port 8082)  │
  │ JWT Filter   │          │ JWT Filter   │
  │ SecurityCfg  │          │ SecurityCfg  │
  └──────────────┘          └──────────────┘
        │                            │
        └────────────┬───────────────┘
                     │
                     v
            PostgreSQL (RDS)
```

### Validación Dual de JWT

**Nivel 1 - AWS API Gateway**:
- Valida JWT de Azure AD
- Verifica issuer/audience
- Comprueba expiración

**Nivel 2 - BFF**:
- Valida JWT nuevamente
- Extrae claims de usuario
- Pasa token a microservicios

**Nivel 3 - Microservicios**:
- Validan JWT por tercera vez
- Extraen roles de claims
- Aplican autorización por endpoint

---

## 📊 Manejo de Errores

### Global Exception Handler (BFF)

```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Error no manejado en BFF", ex);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 500,
                "error", "Internal Server Error",
                "message", "Ocurrió un error en BFF",
                "path", request.getDescription(false)
            ));
    }
}
```

### Propagación de Errores

- **4xx Errors**: Se propagan directamente del microservicio
- **5xx Errors**: Se registran y retornan como error del BFF
- **Timeout**: BFF retorna 504 Gateway Timeout
- **Conexión rechazada**: BFF retorna 503 Service Unavailable

---

## 🧪 Testing Local

### Requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Docker (opcional, para PostgreSQL)

### Iniciar todos los servicios

**Terminal 1: PostgreSQL**
```bash
docker run --name tienda-postgres \
    -e POSTGRES_DB=tienda_perritos \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -p 5432:5432 \
    postgres:15
```

**Terminal 2: ms-productos**
```bash
cd tienda-perritos-ms-productos
mvn spring-boot:run
```

**Terminal 3: ms-usuarios**
```bash
cd tienda-perritos-ms-usuarios
mvn spring-boot:run
```

**Terminal 4: BFF**
```bash
cd tienda-perritos-bff
mvn spring-boot:run
```

### Pruebas de Endpoints

**Obtener usuario actual (requiere JWT válido)**:
```bash
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

**Obtener dashboard**:
```bash
curl -X GET http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

**Listar productos**:
```bash
curl -X GET http://localhost:8080/api/v1/productos \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

**Listar usuarios (ADMIN)**:
```bash
curl -X GET http://localhost:8080/api/v1/usuarios \
  -H "Authorization: Bearer <JWT_TOKEN_ADMIN>"
```

---

## 📝 Próximos Pasos

1. ✅ JWT Filters - Implementados en ms-productos y ms-usuarios
2. ✅ BFF Completo - Con aggregación de datos
3. ➡️ **Angular + MSAL Frontend**
   - Login con Azure AD
   - Integración con endpoints del BFF
   - Gestión de tokens

4. ➡️ **Docker Compose**
   - Definir compose.yml para todos los servicios
   - Network compartida
   - Variables de entorno

5. ➡️ **Deployment AWS**
   - Crear EC2 instance
   - Configurar RDS PostgreSQL
   - Setup API Gateway con JWT Authorizer
   - CloudFront + S3 para frontend
   - CI/CD con GitHub Actions

6. ➡️ **Testing & Validation**
   - Tests unitarios (JUnit 5)
   - Tests de integración (TestContainers)
   - Tests E2E (Cypress)
   - Security testing

---

## 📚 Documentación Adicional

- **ARCHITECTURE.md**: Decisiones técnicas y diseño
- **IMPLEMENTACION.md**: Detalles de ms-productos y ms-usuarios
- **Este archivo**: JWT Filters y BFF

---

**Estado**: ✅ JWT Filters + BFF COMPLETADOS - Listos para Angular Frontend
