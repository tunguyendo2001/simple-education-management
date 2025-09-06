#!/bin/bash
# scripts/docker-start.sh - Start the Docker environment

set -e

echo "🚀 Starting Education Department API..."

# Check if .env exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please run ./scripts/docker-setup.sh first"
    exit 1
fi

# Start services
echo "🐳 Starting Docker services..."
docker-compose up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check service health
echo "🔍 Checking service health..."
docker-compose ps

# Show logs
echo "📄 Recent logs:"
docker-compose logs --tail=20 app

echo "✅ Services started successfully!"
echo ""
echo "🌐 Application URLs:"
echo "   API Documentation: http://localhost:8080/swagger-ui.html"
echo "   API Endpoints:     http://localhost:8080/api/"
echo "   Database Admin:    http://localhost:8081 (Adminer)"
echo "   Nginx Proxy:       http://localhost"
echo ""
echo "🔑 Default Login Credentials:"
echo "   Username: thuy.nguyen"
echo "   Password: password123"
echo ""
echo "📊 Useful commands:"
echo "   docker-compose logs -f app    # View app logs"
echo "   docker-compose logs -f mysql  # View database logs"
echo "   docker-compose down           # Stop all services"
