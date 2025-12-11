## Bootcamp Microservicios con Java

### Docker Hands-on Practice
CLI:
```shell
# Verify Docker is running
docker version

# Ensure a Java app is ready for this practice (.jar file)
docker search openjdk

# Pull images
docker pull openjdk:24-jdk-slim
docker pull nginx:latest

# List images
docker images

# Running containers
docker run -it openjdk:24-jdk-slim bash
# Inside the container run
java -version
exit

# Run a detached web server
docker run -d --name webserver -p 8080:80 nginx:latest
# Open http://localhost:8080

# Listing Running Containers
docker ps

# List containers including stopped
docker ps -a

# View logs
docker logs <container_name_or_id>

# Stop containers
docker stop <container_name_or_id>

# Remove stopped containers
docker rm <container_name_or_id>

# Removing images
docker rmi <image_name_or_id>
```

### Containerizing the `empleadoservice` microservice

Create the `Dockerfile`
```dockerfile
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
```

CLI
```shell
# Build an image from the Dockerfile
docker build -t empleadoservice:latest .

# Run the container
docker run -d --name empleadoservice -p 8080:8080 empleadoservice:latest

# View logs
docker logs empleadoservice 

# List the running containers
docker ps

# Stop the container
docker stop empleadoservice 

# Remove the container
docker rm empleadoservice 

# List the images
docker image ls

# Cleanup the images
docker rmi empleadoservice:latest
docker image ls

# Recap
# Commands: pull, run, ps, logs, stop, rm, build, images
# Dockerfile: FROM, WORKDIR, COPY, EXPOSE, CMD
```

Create the `docker-compose.yml` file
docker-compose.yml
```yml
services:
  app:
    image: empleadoservice:latest
    container_name: empleadoservice
    ports:
      - "8080:8080" # Map host port 8080 to container port 8080
    environment:
      # Configure database connection details for the Java app
      # 'db' is the hostname of the postgres service defined below
      # '5432' is the default postgres port
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/empleado
      SPRING_DATASOURCE_USERNAME: cody
      SPRING_DATASOURCE_PASSWORD:
      # Add any other environment variables your app needs
    networks:
      - app-network # Connect this service to the custom network
    depends_on:
      - db # Wait for the 'db' service to start before starting 'app'
  # Service definition for the PostgreSQL database
  db:
    image: postgres:16 # The official PostgreSQL image
    container_name: postgres-db
    environment:
      # These variables initialize the database when the container first starts
      POSTGRES_DB: empleado # Creates the database
      POSTGRES_USER: cody
      POSTGRES_PASSWORD: 
    # Persist database data using a named volume
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - app-network # Connect this service to the custom network
    #ports: # You can optionally expose the DB port to the host for debugging/direct access
    #  - "5432:5432"
# Define the custom network
networks:
  app-network:
    driver: bridge # Use the default bridge network driver
# Define the named volume for data persistence
volumes:
  db-data:
    driver: local
```

CLI
```shell
# Start the app stack
docker-compose up -d

# Check the status
docker-compose ps

# View logs in real-time
docker-compose logs -f app # View logs for the app service in real-time
docker-compose logs -f db  # View logs for the db service in real-time

# Access the empleadoservice on http://localhost:8080

# Stop and remove the containers, network, and volumes
docker-compose down -v

# Explain differences between up, start, down, stop

# Recap and finish
```