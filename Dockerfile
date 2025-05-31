# Use Ubuntu 22.04 LTS
FROM ubuntu:22.04

# Set non-interactive frontend to avoid prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install core tools and languages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        # System tools
        curl \
        ca-certificates \
        # Python
        python3 \
        python3-pip \
        python3-venv \
        # Java
        openjdk-17-jdk \
        maven \
        # Node.js
        nodejs \
        npm \
        # C/C++
        gcc \
        g++ \
        make \
        # Web server
        nginx \
        # Cleanup
        && apt-get clean \
        && rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN useradd -m coder && \
    mkdir -p /workspace && \
    chown coder:coder /workspace

# Set up workspace
WORKDIR /workspace
COPY entrypoint.sh /usr/local/bin/run
RUN chmod +x /usr/local/bin/run

# Environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="/home/coder/.local/bin:${PATH}"

# Switch to non-root user
USER coder

# Install global Node.js packages (if needed)
# RUN npm install -g yarn

# Expose common ports:
# - 80: Nginx
# - 1234: Custom app port
# - 3000: Node.js
# - 8080: Spring Boot
EXPOSE 80 1234 3000 8080

ENTRYPOINT ["/usr/local/bin/run"]






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


