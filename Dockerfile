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


# Multi-stage build for optimized image size
FROM ubuntu:22.04 as builder

# Install all build dependencies
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
        dos2unix \
        rustc \
        golang \
        php \
        ruby \
    && rm -rf /var/lib/apt/lists/*

# Copy and prepare runner script
COPY entrypoint.sh /tmp/
RUN dos2unix /tmp/entrypoint.sh && \
    chmod 755 /tmp/entrypoint.sh && \
    mv /tmp/entrypoint.sh /usr/local/bin/run

# --- Runtime image ---
FROM ubuntu:22.04

# Install only runtime dependencies
RUN apt-get update && \
    apt-get install -y \
        python3 \
        openjdk-17-jre \
        gcc \
        g++ \
        nodejs \
        nginx \
        rustc \
        golang \
        php \
        ruby \
    && rm -rf /var/lib/apt/lists/*

# Copy prepared runner from builder
COPY --from=builder /usr/local/bin/run /usr/local/bin/run

# Environment configuration
ENV PORT=80
WORKDIR /workspace
EXPOSE 80 1234

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost/ || exit 1

# Secure entrypoint
ENTRYPOINT ["/bin/bash", "/usr/local/bin/run"]
