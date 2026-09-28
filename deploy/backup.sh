#!/usr/bin/env bash
# Nightly Postgres backup: pg_dump -> local retention (7 days) -> upload to
# OCI Object Storage. Intended to run via cron inside/alongside the
# docker-compose stack.
#
# Install (run once on the host, as the deploying user):
#   sudo crontab -e   # or: crontab -e
#   0 2 * * * /opt/ambikarealestate-backend/deploy/backup.sh >> /var/log/ambika-backup.log 2>&1
#
# Assumption: the OCI CLI is installed and configured (`oci setup config`)
# for the user running this cron job, and OCI_BUCKET/OCI_NAMESPACE/OCI_REGION
# are set in the .env file below.

set -euo pipefail

APP_DIR="${APP_DIR:-/opt/ambikarealestate-backend}"
BACKUP_DIR="${BACKUP_DIR:-/opt/ambikarealestate-backend/backups}"
RETENTION_DAYS="${RETENTION_DAYS:-7}"

# shellcheck disable=SC1091
set -a; source "${APP_DIR}/.env"; set +a

mkdir -p "$BACKUP_DIR"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
FILENAME="ambikaestate_${TIMESTAMP}.dump"
FILEPATH="${BACKUP_DIR}/${FILENAME}"

echo "[$(date -Iseconds)] Starting backup -> ${FILEPATH}"
docker compose -f "${APP_DIR}/docker-compose.yml" exec -T postgres \
  pg_dump -Fc --no-owner -U "${POSTGRES_USERNAME:-postgres}" "${POSTGRES_DB:-AmbikaEstate}" > "$FILEPATH"

echo "[$(date -Iseconds)] Uploading to OCI Object Storage bucket ${OCI_BUCKET}"
oci os object put \
  --namespace "${OCI_NAMESPACE}" \
  --region "${OCI_REGION}" \
  --bucket-name "${OCI_BUCKET}" \
  --file "$FILEPATH" \
  --name "db-backups/${FILENAME}" \
  --force

echo "[$(date -Iseconds)] Pruning local backups older than ${RETENTION_DAYS} days"
find "$BACKUP_DIR" -name '*.dump' -type f -mtime "+${RETENTION_DAYS}" -delete

echo "[$(date -Iseconds)] Pruning remote backups older than ${RETENTION_DAYS} days"
CUTOFF_EPOCH=$(( $(date +%s) - RETENTION_DAYS * 86400 ))
oci os object list --namespace "${OCI_NAMESPACE}" --bucket-name "${OCI_BUCKET}" \
  --prefix "db-backups/" --all --query "data[].{name:name,timeCreated:\"time-created\"}" \
  --output table 2>/dev/null | true
# Note: OCI Object Storage lifecycle rules are the recommended way to expire
# remote objects automatically; configure a 7-day delete rule on the bucket
# via the console/Terraform instead of relying solely on this script.

echo "[$(date -Iseconds)] Backup complete: ${FILENAME}"
