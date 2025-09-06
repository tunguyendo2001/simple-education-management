#!/bin/bash
# scripts/docker-stop.sh - Stop the Docker environment

set -e

echo "🛑 Stopping Education Department API..."

# Stop services
docker-compose down

echo "✅ All services stopped successfully!"
