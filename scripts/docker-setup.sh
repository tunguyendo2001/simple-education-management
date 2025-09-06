#!/bin/bash
# scripts/docker-setup.sh - Setup script for Docker environment

set -e

echo "🐳 Education Department API Docker Setup"
echo "========================================"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create necessary directories
echo "📁 Creating directories..."
mkdir -p docker/mysql/init
mkdir -p docker/mysql/conf
mkdir -p docker/mysql/logs
mkdir -p docker/nginx/conf.d
mkdir -p logs
mkdir -p uploads

# Make scripts executable
echo "🔧 Making scripts executable..."
chmod +x scripts/*.sh
chmod +x docker/wait-for-it.sh

# Copy environment file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Please edit .env file with your settings before running docker-compose up"
fi

# Build and start services
echo "🏗️  Building and starting services..."
docker-compose build

echo "✅ Setup completed! You can now run:"
echo "   docker-compose up -d    # Start in background"
echo "   docker-compose logs -f  # View logs"
echo "   docker-compose down     # Stop services"
