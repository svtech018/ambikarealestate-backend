# Cutover Checklist — Railway → Oracle Cloud

1. Lower DNS TTL for `api.ambikarealestate.com` to 60s at least 24h before cutover.
2. Provision OCI ARM A1 instance, run `deploy/setup.sh`, deploy stack with `.env` filled in.
3. Run `deploy/migrate-db.sh` (RAILWAY_DATABASE_URL, TARGET_DATABASE_URL) for a rehearsal sync; fix mismatches.
4. Freeze writes on Railway (maintenance mode / scale API to 0) to guarantee zero data loss.
5. Re-run `deploy/migrate-db.sh` for the final delta sync; confirm all tables show `OK`.
6. Start the OCI stack (`docker compose up -d`); verify `curl -s localhost/actuator/health` is `UP`.
7. Update DNS A record for `api.ambikarealestate.com` → OCI instance public IP.
8. Wait for TTL to expire; confirm propagation (`dig api.ambikarealestate.com`).
9. Update Vercel env var `NEXT_PUBLIC_API_URL` (or equivalent) → `https://api.ambikarealestate.com`, redeploy.
10. Update Sanity Studio CORS origins to include `https://ambikarealestate.com` / new API host if used server-side.
11. Smoke test: login, list properties, submit inquiry, image upload/view, health + metrics endpoints.
12. Keep Railway running (read-only) for 24–48h as rollback; revert DNS A record if smoke tests fail.
13. After confidence window, decommission Railway service and database.
14. Restore original DNS TTL once stable.
