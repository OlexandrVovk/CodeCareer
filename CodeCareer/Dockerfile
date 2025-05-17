# Multi-stage Dockerfile for building and serving the CodeCareer Compose App

# Stage 1: build the Compose Multiplatform WASM application
FROM node:18-slim AS builder

# Install JDK for Kotlin/Gradle build
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-17-jdk-headless \
    && rm -rf /var/lib/apt/lists/*


WORKDIR /app

# Copy project files
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY package.json package-lock.json ./
COPY kotlin-js-store kotlin-js-store
COPY composeApp composeApp

# Ensure the Gradle wrapper is executable
RUN chmod +x gradlew

# Build the production distribution
RUN ./gradlew build

# Stage 2: serve the production build with nginx
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built static files
COPY --from=builder /app/composeApp/build/dist/wasmJs/productionExecutable/ /usr/share/nginx/html/

# Expose port and start nginx
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]