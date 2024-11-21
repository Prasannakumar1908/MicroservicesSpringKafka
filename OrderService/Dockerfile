# Use an official Java runtime as a base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/OrderService-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your microservice uses
EXPOSE 8083

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
