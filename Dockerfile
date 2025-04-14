# -------- Stage 1: Build JAR --------
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy source code
COPY . .

# Build JAR file
RUN ./mvnw clean package -DskipTests

# -------- Stage 2: Run App --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
