# Complete Deployment Guide - Tienda de Perritos

## 📋 Table of Contents
1. [Local Development Setup](#local-development-setup)
2. [Docker Compose](#docker-compose)
3. [AWS Deployment](#aws-deployment)
4. [CI/CD Pipeline](#cicd-pipeline)
5. [Configuration & Secrets](#configuration--secrets)

---

## 🏠 Local Development Setup

### Prerequisites
- Docker & Docker Compose
- Node.js 20+ (for Angular)
- Java 21+ (for Spring Boot)
- Maven 3.9+
- PostgreSQL 15+

### Quick Start

```bash
# Clone repository
git clone <repo-url>
cd tienda-perritos

# Start all services with Docker Compose
docker-compose up -d

# Services will be available at:
# Frontend:     http://localhost:80
# BFF:          http://localhost:8080
# ms-productos: http://localhost:8081
# ms-usuarios:  http://localhost:8082
# PostgreSQL:   localhost:5432
```

### Manual Development Setup

```bash
# Terminal 1: PostgreSQL
docker run --name tienda-postgres \
  -e POSTGRES_DB=tienda_perritos \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# Terminal 2: ms-productos
cd tienda-perritos-ms-productos
mvn spring-boot:run

# Terminal 3: ms-usuarios
cd tienda-perritos-ms-usuarios
mvn spring-boot:run

# Terminal 4: BFF
cd tienda-perritos-bff
mvn spring-boot:run

# Terminal 5: Frontend
cd tienda-perritos-angular
npm install
npm start
# Open http://localhost:4200
```

---

## 🐳 Docker Compose

### File Structure
```
docker-compose.yml
├── postgres (PostgreSQL 15)
├── ms-productos (Spring Boot)
├── ms-usuarios (Spring Boot)
├── bff (Spring Boot)
└── frontend (Angular + Nginx)
```

### Services

**postgres**
- Port: 5432
- Database: tienda_perritos
- User: postgres
- Password: postgres
- Volume: postgres_data

**ms-productos**
- Port: 8081
- Context: /api/v1/productos
- Depends on: postgres

**ms-usuarios**
- Port: 8082
- Context: /api/v1/usuarios
- Depends on: postgres

**bff**
- Port: 8080
- Context: /api/v1
- Proxies to: ms-productos, ms-usuarios

**frontend**
- Port: 80
- Built Angular app
- Nginx reverse proxy

### Environment Variables

Create `.env` file in project root:

```envñ
# OAuth2 Configuration
MSAL_CLIENT_ID=<your-client-id>
MSAL_TENANT_ID=<your-tenant-id>

# Database (optional, uses defaults if not set)
DATABASE_HOST=postgres
DATABASE_PORT=5432
DATABASE_NAME=tienda_perritos
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres

# Microservices URLs
MS_PRODUCTOS_URL=http://ms-productos:8081/api/v1
MS_USUARIOS_URL=http://ms-usuarios:8082/api/v1
```

### Common Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f bff

# Stop all services
docker-compose down

# Rebuild images
docker-compose build --no-cache

# Scale specific service
docker-compose up -d --scale ms-productos=2
```

---

## ☁️ AWS Deployment

### Architecture Overview

```
Internet
   ↓
CloudFront (CDN + Cache)
   ↓
S3 Bucket (Static Frontend)
   ↓
API Gateway (JWT Validation)
   ↓
Application Load Balancer
   ↓
ECS Cluster
├── Task: ms-productos
├── Task: ms-usuarios
└── Task: bff
   ↓
RDS PostgreSQL (Multi-AZ)
```

### Prerequisites

- AWS Account with appropriate permissions
- AWS CLI configured
- Terraform installed (v1.0+)
- Docker credentials configured

### Terraform Deployment

```bash
# Initialize Terraform
cd terraform
terraform init

# Review changes
terraform plan -var-file=prod.tfvars

# Apply configuration
terraform apply -var-file=prod.tfvars \
  -var="db_username=admin" \
  -var="db_password=<secure-password>"

# Get outputs
terraform output
```

### Infrastructure Created

1. **VPC**
   - CIDR: 10.0.0.0/16
   - Public subnets: 2 (for ALB)
   - Private subnets: 2 (for ECS, RDS)
   - NAT Gateway for outbound traffic

2. **RDS PostgreSQL**
   - Instance class: db.t3.micro (dev), db.t3.small (prod)
   - Multi-AZ in production
   - Automated backups (7 days dev, 30 days prod)
   - Encryption enabled

3. **ECS Cluster**
   - Container Insights enabled
   - Auto-scaling policies
   - CloudWatch logging

4. **ECR Repositories**
   - tienda-perritos-productos
   - tienda-perritos-usuarios
   - tienda-perritos-bff
   - tienda-perritos-frontend
   - Image scanning on push enabled

5. **CloudFront Distribution**
   - Origin: S3 bucket
   - HTTPS only (redirects HTTP)
   - Cache TTL: 1 hour default
   - Origin Access Identity for security

### Manual AWS Setup (Alternative)

```bash
# 1. Create RDS Instance
aws rds create-db-instance \
  --db-instance-identifier tienda-perritos-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username admin \
  --master-user-password <password> \
  --allocated-storage 20

# 2. Create ECR Repositories
for repo in productos usuarios bff frontend; do
  aws ecr create-repository \
    --repository-name tienda-perritos-$repo \
    --region us-east-1
done

# 3. Build and push Docker images
aws ecr get-login-password | docker login \
  --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com

docker build -t tienda-perritos-productos:latest tienda-perritos-ms-productos/
docker tag tienda-perritos-productos:latest \
  <account>.dkr.ecr.us-east-1.amazonaws.com/tienda-perritos-productos:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/tienda-perritos-productos:latest
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

**File**: `.github/workflows/ci-cd.yml`

#### Triggers
- `push` to `main` or `develop`
- `pull_request` to `main` or `develop`

#### Jobs

1. **test** (runs on every push/PR)
   - Spins up PostgreSQL container
   - Runs Maven tests for all services
   - Runs on: ubuntu-latest

2. **build-backend** (runs on push to main/develop)
   - Builds Docker images for:
     - ms-productos
     - ms-usuarios
     - bff
   - Pushes to ECR with tags: `commit-sha` and `latest`

3. **build-frontend** (runs on push to main/develop)
   - Installs Node dependencies
   - Builds Angular app (production mode)
   - Builds Docker image with Nginx
   - Pushes to ECR

4. **deploy-dev** (runs on push to develop)
   - Updates ECS service in dev cluster
   - Forces new deployment

5. **deploy-prod** (runs on push to main)
   - Requires `production` environment approval
   - Updates ECS service in prod cluster
   - Invalidates CloudFront cache

### Required Secrets in GitHub

```
AWS_ACCESS_KEY_ID          # IAM user with ECR/ECS permissions
AWS_SECRET_ACCESS_KEY      # IAM user secret key
AWS_ACCOUNT_ID             # Your AWS account ID
CLOUDFRONT_DIST_ID         # CloudFront distribution ID
```

### Setting Up Secrets

```bash
# In GitHub repository:
# Settings → Secrets and variables → Actions → New repository secret

# Add these secrets:
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_ACCOUNT_ID
CLOUDFRONT_DIST_ID
```

### Pipeline Flow

```
Push to GitHub
    ↓
Tests (Java services)
    ↓
[Only on main/develop]
    ├── Build Backend Images
    ├── Build Frontend Image
    ↓
[Only on develop] → Deploy to Dev
[Only on main] → Approve → Deploy to Prod
```

### Monitoring Pipeline

```bash
# View workflow runs
gh run list

# View specific run
gh run view <run-id>

# View workflow file
gh workflow view ci-cd.yml

# Re-run a workflow
gh run rerun <run-id>
```

---

## 🔐 Configuration & Secrets

### Environment Variables

#### Development (Local)
```env
NODE_ENV=development
API_URL=http://localhost:8080/api/v1
MSAL_CLIENT_ID=dev-client-id
MSAL_TENANT_ID=dev-tenant-id
DATABASE_HOST=localhost
DATABASE_PORT=5432
```

#### Production (AWS)
```env
NODE_ENV=production
API_URL=https://api.tienda-perritos.com/api/v1
MSAL_CLIENT_ID=prod-client-id
MSAL_TENANT_ID=prod-tenant-id
DATABASE_HOST=rds-endpoint
DATABASE_PORT=5432
```

### AWS Secrets Manager

Store sensitive data in AWS Secrets Manager:

```bash
# Create secret for database credentials
aws secretsmanager create-secret \
  --name tienda-perritos/db/credentials \
  --secret-string '{
    "username": "admin",
    "password": "secure-password"
  }'

# Create secret for Azure AD
aws secretsmanager create-secret \
  --name tienda-perritos/azure-ad \
  --secret-string '{
    "client_id": "your-client-id",
    "tenant_id": "your-tenant-id"
  }'
```

### Local Development Secrets

Create `secrets.local.env`:
```env
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
MSAL_CLIENT_ID=local-client-id
```

Never commit this file (add to `.gitignore`).

---

## 📊 Monitoring & Logs

### CloudWatch Logs

```bash
# View logs for specific service
aws logs tail /ecs/tienda-perritos-bff --follow

# View logs for date range
aws logs filter-log-events \
  --log-group-name /ecs/tienda-perritos-productos \
  --start-time $(date -d '1 hour ago' +%s)000
```

### ECS Monitoring

```bash
# View ECS service status
aws ecs describe-services \
  --cluster tienda-perritos-prod \
  --services tienda-perritos-app

# View task status
aws ecs list-tasks \
  --cluster tienda-perritos-prod

# View task logs
aws ecs describe-tasks \
  --cluster tienda-perritos-prod \
  --tasks <task-arn>
```

### CloudFront Monitoring

```bash
# View CloudFront distribution statistics
aws cloudfront get-distribution-statistics \
  --id <distribution-id> \
  --start-time <timestamp> \
  --end-time <timestamp>
```

---

## 🚨 Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| Containers not starting | Check `docker-compose logs <service>` |
| Database connection refused | Ensure postgres container is healthy |
| CORS errors | Verify API URL in environment config |
| Deployment stuck | Check IAM permissions in AWS console |
| Frontend shows 404 | Verify Nginx configuration, check S3 bucket |

### Debug Commands

```bash
# Check service health
docker-compose ps

# View network
docker network ls
docker network inspect tienda-perritos_tienda-network

# Test connectivity
docker-compose exec bff curl http://ms-productos:8081/api/v1/productos

# View environment variables
docker-compose exec bff printenv | grep DATABASE
```

---

## 📈 Scaling & Performance

### Horizontal Scaling (Docker Compose)
```bash
docker-compose up -d --scale ms-productos=3
```

### Vertical Scaling (AWS ECS)
```bash
# Update task definition with larger memory/CPU
aws ecs register-task-definition \
  --family tienda-perritos-app \
  --container-definitions file://task-def.json
```

### Auto-Scaling (AWS)
```bash
# Create auto-scaling policy
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/tienda-perritos-prod/tienda-perritos-app \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10
```

---

## ✅ Deployment Checklist

- [ ] Secrets configured in GitHub Actions
- [ ] AWS IAM roles created with proper permissions
- [ ] RDS database created and secure
- [ ] ECR repositories created
- [ ] CloudFront distribution configured
- [ ] S3 bucket for frontend created and configured
- [ ] Domain registered and DNS configured
- [ ] SSL certificate imported (ACM)
- [ ] Tests passing in CI/CD
- [ ] Staging deployment verified
- [ ] Production deployment approved and running
- [ ] Monitoring and alerts configured
- [ ] Backups scheduled for RDS
- [ ] CDN cache invalidation working

---

**Last Updated**: September 2026
**Status**: ✅ Complete - Ready for Production Deployment
