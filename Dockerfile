FROM ubuntu:22.04

# Install compilers/interpreters and tools
RUN apt-get update && \
     apt-get install -y \
        python3 \
        openjdk-17-jdk \
        gcc \
        g++ \
        nodejs \
        npm \
        nginx \
        curl \
    && rm -rf /var/lib/apt/lists/*

# Optionally: install pip, yarn, etc. if needed for more languages

# Copy the universal runner script
COPY entrypoint.sh /usr/local/bin/run
RUN chmod +x /usr/local/bin/run

# Create a working directory for user code
WORKDIR /workspace

# Expose ports (if needed for nginx or your Spring Boot app)
EXPOSE 1234 80

ENTRYPOINT ["/usr/local/bin/run"]


