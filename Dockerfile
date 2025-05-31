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







FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    python3 \
    python3-pip \
    python3-venv \
    openjdk-17-jdk \
    maven \
    gcc \
    g++ \
    make \
    nginx \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js (LTS) from Nodesource (recommended)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Create non-root user and workspace
RUN useradd -m coder && mkdir -p /workspace && chown coder:coder /workspace

WORKDIR /workspace
COPY entrypoint.sh /usr/local/bin/run
RUN chmod +x /usr/local/bin/run

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="/home/coder/.local/bin:${PATH}"

USER coder

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


