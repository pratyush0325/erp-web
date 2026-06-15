# EC2 Deployment Guide

## Prerequisites
- Amazon Linux 2023 or Ubuntu
- Java 21+ (JRE is enough to run the jar)
- MySQL 8.x
- Caddy

## 1. Bootstrap auth_db

Run `database/auth_db.sql` manually once (Flyway manages `erp_db` only):

```bash
mysql -u root -p < database/auth_db.sql
```

## 2. Deploy the backend

```bash
# Build on your local machine
JAVA_HOME=<jdk21-path> mvn package -f pom.xml -DskipTests

# Copy to server
scp target/Uni-1.0-SNAPSHOT.jar ec2-user@<ip>:/srv/erp/app.jar

# Set up secrets
cp deploy/.env.example /srv/erp/.env
# Edit /srv/erp/.env with real values

# Install and start the service
scp deploy/erp.service ec2-user@<ip>:/etc/systemd/system/erp.service
ssh ec2-user@<ip> "sudo systemctl daemon-reload && sudo systemctl enable --now erp"
```

## 3. Deploy the frontend

```bash
cd erp-web
npm run build
scp -r dist/ ec2-user@<ip>:/srv/erp-web/dist/
```

## 4. Configure Caddy

Edit `deploy/Caddyfile` — replace `your-domain.com` with your actual domain (or `:80` for IP-only).

```bash
scp deploy/Caddyfile ec2-user@<ip>:/etc/caddy/Caddyfile
ssh ec2-user@<ip> "sudo systemctl reload caddy"
```

## EC2 Security Group

| Port | Purpose |
|------|---------|
| 22   | SSH |
| 80   | HTTP (Caddy redirects to HTTPS) |
| 443  | HTTPS |

Ports 8080 (Spring Boot) and 3306 (MySQL) must NOT be exposed — they listen on localhost only.

## Updating

```bash
# Backend
mvn package -DskipTests
scp target/Uni-1.0-SNAPSHOT.jar ec2-user@<ip>:/srv/erp/app.jar
ssh ec2-user@<ip> "sudo systemctl restart erp"

# Frontend
cd erp-web && npm run build
scp -r dist/ ec2-user@<ip>:/srv/erp-web/dist/
```
