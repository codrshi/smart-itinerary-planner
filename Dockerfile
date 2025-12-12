# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .
RUN mvn -q -e -B dependency:resolve dependency:resolve-plugins

# Copy source and build
COPY src ./src
RUN mvn -q -e -B package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-jammy

# Create non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Use production profile
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
