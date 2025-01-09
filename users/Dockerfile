# Start with a base image containing Java
FROM openjdk:17

# Set the working directory
WORKDIR /app

ARG JAR_FILE=target/*.jar

# Copy the application JAR file
COPY ${JAR_FILE} app.jar

# Copy the kafka_client_jaas.conf file
COPY src/main/resources/kafka_client_jaas.conf /app/kafka_client_jaas.conf


# Expose the necessary port (optional, if your app listens on a port)
EXPOSE 8080

# Set the JVM argument for the JAAS configuration and run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
