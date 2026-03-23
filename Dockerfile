# ================================
# Stage 1: Cache dependencies
# ================================
FROM maven:3.9.8-eclipse-temurin-21 AS deps

WORKDIR /app

# Copy only pom.xml first to leverage Docker layer caching.
# Dependencies are re-downloaded ONLY when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# ================================
# Stage 2: Build
# ================================
FROM deps AS build

# Copy source code and build
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

# ================================
# Stage 3: Runtime
# ================================
# Use JRE instead of JDK — significantly smaller image size
FROM eclipse-temurin:21-jre-alpine

# Security: run as non-root user
#RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Set ownership to non-root user
#RUN chown appuser:appgroup app.jar

#USER appuser

EXPOSE 8000

# Use exec form with JVM optimizations for containerized environments
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar", \
  "--spring.profiles.active=prod"]