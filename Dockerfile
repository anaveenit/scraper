# Use a valid Maven image with OpenJDK 17 to build the project
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and all source code to the working directory
COPY pom.xml .
COPY src ./src

# Package the application into a jar file using Maven
RUN mvn clean package -DskipTests

# Verify if the JAR file exists after the build
RUN ls -l /app/target  # List the contents of the target directory for debugging

# Use an OpenJDK runtime image to run the Spring Boot application
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Ensure the target directory contains the generated JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
