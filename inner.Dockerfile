FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

# Install required compilers and runtimes
RUN apt-get update && apt-get install -y \
    build-essential \
    openjdk-17-jdk \
    python3 \
    python3-pip \
    nodejs \
    npm \
    curl \
    gcc \
    g++ \
    git \
    && apt-get clean

# Set python3 as default
RUN update-alternatives --install /usr/bin/python python /usr/bin/python3 1

# Setup working directory
WORKDIR /app

# Copy all project files
COPY . .

# Copy and set permissions for entrypoint
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Final command to run your entrypoint
CMD ["/entrypoint.sh"]
