# Use an OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the jar built by Maven
COPY target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8989

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
