# Use an official OpenJDK runtime as a parent image
# Using JRE is smaller than JDK if you only need to run compiled code
FROM azul/zulu-openjdk:24-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged application JAR file into the container at /app
# Adjust the source path as needed
COPY target/empleadoservice-0.0.1-SNAPSHOT.jar app.jar

# Make port 8080 available to the world outside this container
# This doesn't actually publish the port, just documents it
EXPOSE 8080

# Define the command to run your application
# This is the command that will run when the container starts
CMD ["java", "-jar", "app.jar"]