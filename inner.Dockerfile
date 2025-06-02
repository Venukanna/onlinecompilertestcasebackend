# inner.Dockerfile
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy everything from the repo into container
COPY . .

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean install

# Expose port (optional)
EXPOSE 8080

# Run the Spring Boot app
CMD ["mvn", "spring-boot:run"]
