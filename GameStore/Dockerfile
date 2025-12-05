# --- Build stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Bind Spring Boot to 0.0.0.0 so Render can reach it
ENTRYPOINT ["java","-Dserver.port=8080","-Dserver.address=0.0.0.0","-jar","app.jar"]
