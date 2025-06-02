FROM ubuntu:20.04

# Prevent prompts during build
ENV DEBIAN_FRONTEND=noninteractive

# Install essential tools and languages
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    openjdk-17-jdk \
    python3 \
    python3-pip \
    nodejs \
    npm \
    g++ \
    git \
    && apt-get clean

# Set default Java and Python
RUN update-alternatives --install /usr/bin/python python /usr/bin/python3 1

# Optional: Install C/C++ compilers
RUN apt-get install -y gcc g++

# Optional: Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Default command (optional)
CMD [ "bash" ]
