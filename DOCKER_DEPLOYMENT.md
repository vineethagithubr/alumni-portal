# Docker Deployment Guide for Alumni Portal

## Overview
This guide explains how to deploy the Alumni Portal application using Docker on Render.

## Files Added
- `Dockerfile` - Multi-stage build configuration
- `.dockerignore` - Excludes unnecessary files from Docker build context

## Docker Configuration
The Dockerfile uses a multi-stage build:
1. **Build Stage**: Uses Maven to compile and package the application
2. **Runtime Stage**: Uses OpenJDK 21 to run the application

## Environment Variables for Render
In your Render Docker service, set these environment variables:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db-name?ssl=true&sslmode=require
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
```

## Deployment Steps

### 1. Push to GitHub
Make sure all files are committed and pushed to your GitHub repository.

### 2. Create Render Docker Service
1. Go to Render Dashboard
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Select "Docker" as the runtime
5. Set environment variables for database connection
6. Click "Create Web Service"

### 3. Database Configuration
Ensure your PostgreSQL database on Render has:
- SSL enabled (required by the connection string)
- Proper connection details matching your environment variables

## Health Checks
The application includes:
- Spring Boot Actuator for health endpoints
- Docker health check at `/actuator/health`
- Automatic restart on failure

## Port Configuration
- Application runs on port 8080 (internal)
- Render will expose this on port 443 (external HTTPS)

## Build Process
The Docker build process:
1. Downloads Maven dependencies
2. Compiles the Java code
3. Runs tests (skipped for faster builds)
4. Packages the application as a JAR
5. Creates a runtime image with the JAR

## Troubleshooting
- Check Render logs for build errors
- Verify database connection details
- Ensure all required files are in the repository
- Check that the database allows SSL connections
