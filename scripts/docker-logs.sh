#!/bin/bash
# scripts/docker-logs.sh - View logs for specific service

SERVICE=${1:-app}

echo "📄 Viewing logs for service: $SERVICE"
echo "Press Ctrl+C to exit"
echo "=========================="

docker-compose logs -f $SERVICE
