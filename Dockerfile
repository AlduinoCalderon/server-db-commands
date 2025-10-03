# Multi-stage build for Spring Boot + Vaadin application
FROM maven:3.8.6-openjdk-11 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application with Vaadin production mode
RUN mvn clean package -Pproduction -DskipTests

# Production stage
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/server-db-commands-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "app.jar"]
