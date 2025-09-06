#!/bin/bash
# scripts/generate-password-hash.sh - Generate BCrypt password hash

if [ $# -eq 0 ]; then
    echo "Usage: $0 <password>"
    exit 1
fi

PASSWORD=$1

# Generate BCrypt hash using Python (if available)
if command -v python3 &> /dev/null; then
    python3 -c "
import bcrypt
password = '$PASSWORD'.encode('utf-8')
hashed = bcrypt.hashpw(password, bcrypt.gensalt())
print(hashed.decode('utf-8'))
"
else
    echo "❌ Python3 with bcrypt is required to generate password hash"
    echo "Install with: pip3 install bcrypt"
    exit 1
fi