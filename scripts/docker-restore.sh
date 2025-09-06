#!/bin/bash
# scripts/docker-restore.sh - Restore database from backup

set -e

if [ $# -eq 0 ]; then
    echo "Usage: $0 <backup_file.sql.gz>"
    echo "Available backups:"
    ls -la backups/
    exit 1
fi

BACKUP_FILE=$1

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "🔄 Restoring database from: $BACKUP_FILE"

# Decompress and restore
if [[ $BACKUP_FILE == *.gz ]]; then
    gunzip -c "$BACKUP_FILE" | docker-compose exec -T mysql mysql -u root -prootpassword education_db
else
    cat "$BACKUP_FILE" | docker-compose exec -T mysql mysql -u root -prootpassword education_db
fi

echo "✅ Database restored successfully!"
