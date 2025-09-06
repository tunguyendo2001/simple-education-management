#!/bin/bash
# scripts/docker-backup.sh - Backup database

set -e

BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="education_db_backup_${TIMESTAMP}.sql"

echo "💾 Creating database backup..."

# Create backup directory
mkdir -p $BACKUP_DIR

# Create database backup
docker-compose exec -T mysql mysqldump -u root -prootpassword education_db > "${BACKUP_DIR}/${BACKUP_FILE}"

# Compress backup
gzip "${BACKUP_DIR}/${BACKUP_FILE}"

echo "✅ Backup created: ${BACKUP_DIR}/${BACKUP_FILE}.gz"
