# Stage 1: Build your project with Maven + JDK
FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /app

# Copy everything to /app
COPY . .

# Run Maven clean package, skip tests for faster build
RUN mvn clean package -DskipTests

# Stage 2: Runtime image with Java + Python + Node + GCC + others
FROM openjdk:17-jdk-slim

# Install required tools for multi-language compiling/execution
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 python3-pip python3-venv \
    nodejs npm \
    gcc g++ make \
    curl \
    nginx \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/compiler-backend-0.0.1-SNAPSHOT.jar app.jar


# Copy your entrypoint.sh script and make it executable
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

EXPOSE 8080

# Run your Spring Boot jar
ENTRYPOINT ["java", "-jar", "app.jar"]





# Build stage: compile jar with Maven + JDK
# FROM maven:3.8.1-openjdk-17 AS build

# WORKDIR /app

# COPY . .

# RUN mvn clean package -DskipTests

# # Runtime stage: openjdk + python + node + gcc + others for code execution
# FROM openjdk:17-jdk-slim

# # Install required tools for multi-language compiling/execution
# RUN apt-get update && apt-get install -y --no-install-recommends \
#     python3 python3-pip python3-venv \
#     nodejs npm \
#     gcc g++ make \
#     curl \
#     nginx \
#     && apt-get clean && rm -rf /var/lib/apt/lists/*

# WORKDIR /app

# # Copy jar file from build stage
# COPY --from=build /app/target/onlinecompilertestcasebackend-0.0.1-SNAPSHOT.jar app.jar

# # Copy your entrypoint.sh script and make executable
# COPY entrypoint.sh /usr/local/bin/entrypoint.sh
# RUN chmod +x /usr/local/bin/entrypoint.sh

# EXPOSE 8080

# ENTRYPOINT ["java", "-jar", "app.jar"]



# # Use Ubuntu 22.04 LTS
# FROM ubuntu:22.04

# # Avoid interactive prompts during package install
# ENV DEBIAN_FRONTEND=noninteractive

# # Install all required languages and tools
# RUN apt-get update && \
#     apt-get install -y --no-install-recommends \
#         curl \
#         ca-certificates \
#         python3 \
#         python3-pip \
#         python3-venv \
#         openjdk-17-jdk \
#         maven \
#         nodejs \
#         npm \
#         gcc \
#         g++ \
#         make \
#         nginx && \
#     apt-get clean && \
#     rm -rf /var/lib/apt/lists/*

# # Create workspace directory
# WORKDIR /workspace

# # Copy the runner script into the image
# COPY entrypoint.sh /usr/local/bin/run
# RUN chmod +x /usr/local/bin/run

# # Expose useful ports
# EXPOSE 80 1234 3000 8080

# # Run the script
# ENTRYPOINT ["/usr/local/bin/run"]







# FROM ubuntu:22.04

# ENV DEBIAN_FRONTEND=noninteractive

# RUN apt-get update && apt-get install -y --no-install-recommends \
#     curl \
#     ca-certificates \
#     python3 \
#     python3-pip \
#     python3-venv \
#     openjdk-17-jdk \
#     maven \
#     gcc \
#     g++ \
#     make \
#     nginx \
#     && apt-get clean \
#     && rm -rf /var/lib/apt/lists/*

# # Install Node.js (LTS) from Nodesource (recommended)
# RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
#     apt-get install -y nodejs && \
#     apt-get clean && rm -rf /var/lib/apt/lists/*

# # Create non-root user and workspace
# RUN useradd -m coder && mkdir -p /workspace && chown coder:coder /workspace

# WORKDIR /workspace
# COPY entrypoint.sh /usr/local/bin/run
# RUN chmod +x /usr/local/bin/run

# ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
# ENV PATH="/home/coder/.local/bin:${PATH}"

# USER coder

# EXPOSE 80 1234 3000 8080

# ENTRYPOINT ["/usr/local/bin/run"      ] 








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


