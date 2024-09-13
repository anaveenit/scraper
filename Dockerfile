# Use an official Maven image to build the project
FROM maven:3.8.6-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and all source code to the working directory
COPY pom.xml .
COPY src ./src

# Package the application into a jar file using Maven
RUN mvn clean package -DskipTests

# Use an OpenJDK runtime image to run the Spring Boot application
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage to the runtime image
COPY --from=build /app/target/*.jar app.jar  # Changed to use wildcard to pick up any generated JAR

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
