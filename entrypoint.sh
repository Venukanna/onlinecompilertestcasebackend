# #!/bin/bash

# # Create a temp directory for this run
# WORKDIR=$(mktemp -d)
# cp "$1" "$WORKDIR/"
# cd "$WORKDIR" || exit 1

# case "$1" in
#     *.java)
#         javac "$(basename "$1")" && java -cp . "$(basename "$1" .java)"
#         ;;
#     *.py)
#         python3 "$(basename "$1")"
#         ;;
#     *.c)
#         gcc "$(basename "$1")" -o main && ./main
#         ;;
#     *.cpp)
#         g++ "$(basename "$1")" -o main && ./main
#         ;;
#     *.js)
#         node "$(basename "$1")"
#         ;;
#     *.html|*.css)
#         cp -- * /usr/share/nginx/html/
#         nginx -g "daemon off;"
#         ;;
#     *)
#         echo "Unsupported file type"
#         exit 1
#         ;;
# esac

# # Cleanup
# rm -rf "$WORKDIR"



#!/usr/bin/env bash

# Improved Code Runner Script
# Supports: Java, Python, C, C++, JavaScript, HTML/CSS, Rust, Go, PHP, Ruby

set -euo pipefail  # Enable strict mode

# Logging function
log() {
    echo "[$(date '+%Y-%m-%d %T')] $1"
}

# Check if file argument is provided
if [[ -z "$1" ]]; then
    log "ERROR: No file provided"
    exit 1
fi

# Validate file exists
if [[ ! -f "$1" ]]; then
    log "ERROR: File '$1' does not exist"
    exit 1
fi

# Create isolated workspace
WORKDIR=$(mktemp -d)
trap 'rm -rf "$WORKDIR"' EXIT  # Ensure cleanup on exit

log "Preparing workspace in $WORKDIR"
cp "$1" "$WORKDIR/"
cd "$WORKDIR" || exit 1

filename=$(basename "$1")
filetype="${filename##*.}"

log "Executing $filename ($filetype)"

case "$filename" in
    *.java)
        javac "$filename" && java -cp . "${filename%.java}"
        ;;
    *.py)
        python3 "$filename"
        ;;
    *.c)
        gcc "$filename" -o main -Wall -Wextra && ./main
        ;;
    *.cpp)
        g++ "$filename" -o main -Wall -Wextra && ./main
        ;;
    *.js)
        node "$filename"
        ;;
    *.html|*.css)
        mkdir -p /usr/share/nginx/html
        cp "$filename" /usr/share/nginx/html/
        log "Starting nginx server"
        nginx -g "daemon off;"
        ;;
    *.rs)
        rustc "$filename" -o main && ./main
        ;;
    *.go)
        go run "$filename"
        ;;
    *.php)
        php "$filename"
        ;;
    *.rb)
        ruby "$filename"
        ;;
    *.sh)
        bash "$filename"
        ;;
    *)
        log "ERROR: Unsupported file type: $filetype"
        exit 1
        ;;
esac

log "Execution completed successfully"
