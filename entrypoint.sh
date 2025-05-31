
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


#!/bin/bash

# Create a temp directory for this run
WORKDIR=$(mktemp -d)
cp "$1" "$WORKDIR/"
cd "$WORKDIR" || exit 1

case "$1" in
    *.java)
        javac "$(basename "$1")" && java -cp . "$(basename "$1" .java)"
        ;;
    *.py)
        python3 "$(basename "$1")"
        ;;
    *.c)
        gcc "$(basename "$1")" -o main && ./main
        ;;
    *.cpp)
        g++ "$(basename "$1")" -o main && ./main
        ;;
    *.js)
        node "$(basename "$1")"
        ;;
    *.html|*.css)
        cp -- * /usr/share/nginx/html/
        nginx -g "daemon off;"
        ;;
    *)
        echo "Unsupported file type"
        exit 1
        ;;
esac

# Cleanup
rm -rf "$WORKDIR"


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



# #!/usr/bin/env bash

# # Universal Code Runner with Enhanced Features
# # Supports: Java, Python, C, C++, JavaScript, HTML/CSS, Rust, Go, PHP, Ruby

# set -euo pipefail  # Strict error handling

# # Colorized logging
# log() {
#     local color=$1; shift
#     printf "\033[${color}m[$(date '+%Y-%m-%d %T')] $*\033[0m\n"
# }

# # Validate input
# [ -z "${1:-}" ] && { log 31 "ERROR: No file provided"; exit 1; }
# [ -f "$1" ] || { log 31 "ERROR: File '$1' not found"; exit 1; }

# # Create isolated workspace (with automatic cleanup)
# WORKDIR=$(mktemp -d -t runner-XXXXXXXXXX)
# trap 'rm -rf "$WORKDIR"' EXIT INT TERM

# log 32 "Setting up workspace in $WORKDIR"
# cp "$1" "$WORKDIR/"
# cd "$WORKDIR" || exit 1

# filename=$(basename "$1")
# extension="${filename##*.}"

# # Memory and time limits (adjust as needed)
# MAX_MEMORY="512M"
# TIMEOUT_SECONDS=15

# log 36 "Executing $filename (Type: $extension)"

# case "$extension" in
#     java)
#         javac "$filename" && \
#         java -Xmx$MAX_MEMORY -cp . "${filename%.java}"
#         ;;
#     py)
#         python3 "$filename"
#         ;;
#     c)
#         gcc "$filename" -o main -Wall -Wextra && \
#         timeout $TIMEOUT_SECONDS ./main
#         ;;
#     cpp)
#         g++ "$filename" -o main -Wall -Wextra && \
#         timeout $TIMEOUT_SECONDS ./main
#         ;;
#     js)
#         node "$filename"
#         ;;
#     html|css)
#         mkdir -p /usr/share/nginx/html
#         cp "$filename" /usr/share/nginx/html/
#         log 34 "Starting nginx on port ${PORT:-80}"
#         exec nginx -g "daemon off;"
#         ;;
#     rs)
#         rustc "$filename" -o main && \
#         timeout $TIMEOUT_SECONDS ./main
#         ;;
#     go)
#         go run "$filename"
#         ;;
#     php)
#         php "$filename"
#         ;;
#     rb)
#         ruby "$filename"
#         ;;
#     sh)
#         bash "$filename"
#         ;;
#     *)
#         log 31 "ERROR: Unsupported file type: $extension"
#         exit 1
#         ;;
# esac

# log 32 "Execution completed successfully"
