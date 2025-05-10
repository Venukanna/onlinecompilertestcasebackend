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

# Check if the file argument is passed
if [ -z "$1" ]; then
    echo "No file provided"
    exit 1
fi

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
