# Tienda de Perritos — Cloud Native Migration

Migración de monolito Node.js/EKS a arquitectura cloud native con Spring Boot, Angular, AWS API Gateway y Azure AD.

**Asignatura**: DSY1107 – Desarrollo Cloud Native I  
**Evaluación**: EP1 (Encargo, 16%) + EP2 (Presentación, 24%)

---

## Estructura del Proyecto

```
tienda-perritos/
├── ARCHITECTURE.md                      # Decisiones técnicas y diseño
├── README.md                            # Este archivo
├── docker-compose.yml                   # Orquestación local (BFF + MS + PostgreSQL)
├── README-DEPLOY.md                     # Pasos de deployment a AWS+Azure (EP2)
│
├── tienda-perritos-ms-productos/        # Microservicio: CRUD de productos
│   ├── pom.xml
│   ├── src/main/java/com/tienda/productos/...
│   ├── src/test/java/...
│   ├── Dockerfile
│   └── README.md
│
├── tienda-perritos-ms-usuarios/         # Microservicio: Gestión de usuarios
│   ├── pom.xml
│   ├── src/main/java/com/tienda/usuarios/...
│   ├── src/test/java/...
│   ├── Dockerfile
│   └── README.md
│
├── tienda-perritos-bff/                 # Backend for Frontend
│   ├── pom.xml
│   ├── src/main/java/com/tienda/bff/...
│   ├── src/test/java/...
│   ├── Dockerfile
│   └── README.md
│
└── tienda-perritos-angular/             # Frontend Angular + MSAL
    ├── package.json
    ├── src/app/...
    ├── angular.json
    ├── Dockerfile (nginx)
    └── README.md
```

---

## Quick Start (Desarrollo Local)

### Requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (o Docker)
- Node.js 18+
- Angular CLI 17+

### 1. Clonar Repos
```bash
cd tienda-perritos
# Cada repo (ms-productos, ms-usuarios, bff, angular) se clona por separado
# o se desarrolla en estas carpetas locales
```

### 2. Compilar Backend
```bash
cd tienda-perritos-ms-productos
mvn clean package
cd ../tienda-perritos-ms-usuarios
mvn clean package
cd ../tienda-perritos-bff
mvn clean package
```

### 3. Ejecutar con Docker Compose
```bash
# Desde raíz del proyecto
docker-compose up -d
```

Endpoints disponibles:
- BFF: `http://localhost:8080`
- ms-productos: `http://localhost:8081`
- ms-usuarios: `http://localhost:8082`
- PostgreSQL: `localhost:5432`

### 4. Frontend (Angular)
```bash
cd tienda-perritos-angular
npm install
ng serve --open
# Acceder a http://localhost:4200
```

---

## Arquitectura

Ver `ARCHITECTURE.md` para decisiones técnicas completas.

**Diagrama Conceptual**:
```
[Angular SPA + MSAL]
    ↓
[AWS API Gateway] ← valida JWT
    ↓
[BFF - Spring Boot] ← valida JWT nuevamente
    ├→ [ms-productos] → PostgreSQL
    └→ [ms-usuarios] → PostgreSQL
```

---

## Requisitos de Evaluación (EP1 + EP2)

### EP1 (Código, 16%)
- ✅ Backend compilable (Maven, buenas prácticas)
- ✅ Filtros JWT en BFF + Microservicios
- ✅ Frontend Angular con MSAL integrado
- ✅ CRUD de productos funcional
- ✅ Autenticación y autorización por roles

### EP2 (Presentación, 24%)
- ✅ API Gateway configurado con rutas y CORS
- ✅ JWT Authorizer en API Gateway (20/100)
- ✅ Tenant Azure AD + app registration
- ✅ OAuth2 Authorization Code + PKCE
- ✅ Evidencia de funcionamiento (llamadas con/sin token)
- ✅ Deployment en EC2 + S3/CloudFront

Ver `README-DEPLOY.md` para pasos detallados.

---

## Stack Tecnológico

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| **Backend** | Spring Boot | 3.3+ |
| **Java** | OpenJDK | 21+ |
| **Build** | Maven | 3.9+ |
| **BD** | PostgreSQL | 15+ |
| **Frontend** | Angular | 17+ |
| **Auth** | Azure AD (MSAL) | v3 |
| **Cloud** | AWS (EC2, API Gateway, S3, RDS) | Latest |
| **Contenedores** | Docker | 24+ |

---

## Guía de Contribución (Flujo de Trabajo)

1. **Crear rama feature**: `git checkout -b feature/<nombre>`
2. **Desarrollar en local**: `mvn clean package` / `ng serve`
3. **Tests obligatorios**: `mvn test`
4. **Commit**: mensajes descriptivos (ej: `feat(ms-productos): add JPA entity`)
5. **Push + PR**: revisión de código antes de merge
6. **Main**: solo código compilable y testeado

---

## Contacto & Soporte

**Docente**: [Nombre]  
**Correo**: [Email institucional]

---

**Última actualización**: 2026-01-09
