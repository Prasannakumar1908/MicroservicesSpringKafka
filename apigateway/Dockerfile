# Use a Java base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the target directory
COPY target/apigateway-0.0.1-SNAPSHOT.jar /app/apigateway.jar

# Expose the port the app will run on
EXPOSE 8082

# Command to run the app
ENTRYPOINT ["java", "-jar", "apigateway.jar"]
