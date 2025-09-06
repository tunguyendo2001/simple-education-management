#!/bin/bash
# scripts/docker-clean.sh - Clean up Docker resources

set -e

echo "🧹 Cleaning up Docker resources..."

# Stop and remove containers
echo "🛑 Stopping containers..."
docker-compose down

# Remove images
echo "🗑️  Removing images..."
docker-compose down --rmi all

# Remove volumes (WARNING: This will delete all data!)
read -p "⚠️  Do you want to remove volumes (this will delete all data)? [y/N] " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️  Removing volumes..."
    docker-compose down -v
    docker volume prune -f
fi

# Remove orphaned containers
echo "🧹 Removing orphaned containers..."
docker container prune -f

# Remove unused networks
echo "🧹 Removing unused networks..."
docker network prune -f

echo "✅ Cleanup completed!"
