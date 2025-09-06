#!/bin/bash
# scripts/docker-shell.sh - Open shell in container

SERVICE=${1:-app}

echo "🐚 Opening shell in service: $SERVICE"

if [ "$SERVICE" = "app" ]; then
    docker-compose exec app /bin/bash
elif [ "$SERVICE" = "mysql" ]; then
    docker-compose exec mysql mysql -u root -prootpassword education_db
else
    docker-compose exec $SERVICE /bin/sh
fi
