# Stage 1: build
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY leyu-admin ./leyu-admin
RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/leyu-admin/target/*.jar app.jar
EXPOSE 8088
CMD ["java", "-jar", "app.jar"]