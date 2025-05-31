# Stage 1: Build the Spring Boot app with Maven
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

# Copy Maven config and source code
COPY pom.xml .
COPY src ./src

# Build jar without tests (optional: skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the Spring Boot app
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose your backend port (adjust if needed)
EXPOSE 8080

# Command to run your Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]






# FROM ubuntu:22.04

# # Install compilers/interpreters and tools
# RUN apt-get update && \
#      apt-get install -y \
#         python3 \
#         openjdk-17-jdk \
#         gcc \
#         g++ \
#         nodejs \
#         npm \
#         nginx \
#         curl \
#     && rm -rf /var/lib/apt/lists/*

# # Optionally: install pip, yarn, etc. if needed for more languages

# # Copy the universal runner script
# COPY entrypoint.sh /usr/local/bin/run
# RUN chmod +x /usr/local/bin/run

# # Create a working directory for user code
# WORKDIR /workspace

# # Expose ports (if needed for nginx or your Spring Boot app)
# EXPOSE 1234 80

# ENTRYPOINT ["/usr/local/bin/run"]


