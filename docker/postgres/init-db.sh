# docker/postgres/init-db.sh

set -e

psql -v ON_ERROR_STOP=1 \
     -v MIGRATION_PASSWORD="$MIGRATION_PASSWORD" \
     -v APP_PASSWORD="$APP_PASSWORD" \
     --username "$POSTGRES_USER" \
     --dbname "$POSTGRES_DB" \
     -f /docker-entrypoint-initdb.d/init-db.sql