# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/OrderService-0.0.1-SNAPSHOT.jar OrderService-0.0.1-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE ${SERVER_PORT:-8083}

# Run the application
ENTRYPOINT ["java", "-jar", "/app/OrderService-0.0.1-SNAPSHOT.jar"]
