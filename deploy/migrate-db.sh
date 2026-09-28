#!/usr/bin/env bash
# Migrate Postgres data from Railway to the new Oracle Cloud host.
#
# Usage:
#   RAILWAY_DATABASE_URL="postgres://user:pass@host:port/db" \
#   TARGET_DATABASE_URL="postgres://user:pass@localhost:5432/AmbikaEstate" \
#   ./deploy/migrate-db.sh
#
# Assumption: TARGET_DATABASE_URL points at the new Postgres container
# (e.g. via a port temporarily published, or run this script from inside
# the docker network / on the OCI host where "postgres" resolves).
#
# Steps: pg_dump (custom format, no owner) from Railway -> pg_restore into
# the target -> per-table row-count verification.

set -euo pipefail

: "${RAILWAY_DATABASE_URL:?Set RAILWAY_DATABASE_URL to the Railway Postgres connection string}"
: "${TARGET_DATABASE_URL:?Set TARGET_DATABASE_URL to the new Postgres connection string}"

DUMP_FILE="${DUMP_FILE:-/tmp/ambikaestate_$(date +%Y%m%d_%H%M%S).dump}"

echo "==> Dumping source database (Railway) to ${DUMP_FILE}"
pg_dump -Fc --no-owner --no-acl -d "$RAILWAY_DATABASE_URL" -f "$DUMP_FILE"

echo "==> Restoring into target database"
# --clean --if-exists allows re-running safely against an already-migrated
# schema (e.g. Flyway already ran there once); adjust if the target is empty.
pg_restore --no-owner --no-acl --clean --if-exists -d "$TARGET_DATABASE_URL" "$DUMP_FILE"

echo "==> Verifying row counts per table"
TABLES=$(psql "$TARGET_DATABASE_URL" -Atc \
  "SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename;")

STATUS=0
printf "%-30s %12s %12s %8s\n" "table" "source" "target" "match"
printf -- "-------------------------------------------------------------------\n"
for t in $TABLES; do
  SRC_COUNT=$(psql "$RAILWAY_DATABASE_URL" -Atc "SELECT count(*) FROM \"$t\";" 2>/dev/null || echo "N/A")
  DST_COUNT=$(psql "$TARGET_DATABASE_URL" -Atc "SELECT count(*) FROM \"$t\";")
  if [ "$SRC_COUNT" = "$DST_COUNT" ]; then
    MATCH="OK"
  else
    MATCH="MISMATCH"
    STATUS=1
  fi
  printf "%-30s %12s %12s %8s\n" "$t" "$SRC_COUNT" "$DST_COUNT" "$MATCH"
done

if [ "$STATUS" -ne 0 ]; then
  echo "!! Row-count mismatch detected. Do NOT cut over DNS until resolved."
  exit 1
fi

echo "==> All tables verified. Dump retained at ${DUMP_FILE} for audit."
