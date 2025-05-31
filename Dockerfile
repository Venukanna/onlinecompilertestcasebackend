# Use an official lightweight Node.js image as base
FROM node:18-slim

# Set working directory
WORKDIR /app

# Update & install required dependencies
RUN apt-get update && \
    apt-get install -y curl git openjdk-17-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy rest of the app
COPY . .

# Expose app port
EXPOSE 3000

# Start the app
CMD ["npm", "start"]





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


