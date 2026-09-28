#!/usr/bin/env bash
# One-time provisioning of an Oracle Cloud Always Free Ampere A1 instance
# (Ubuntu 24.04) to run the docker-compose stack in this repo.
#
# Usage: sudo ./deploy/setup.sh
#
# Assumption: this script is run as root (or via sudo) on a fresh Ubuntu
# 24.04 ARM instance, and the repo has already been cloned to
# /opt/ambikarealestate-backend (adjust APP_DIR below if different).

set -euo pipefail

APP_DIR="${APP_DIR:-/opt/ambikarealestate-backend}"

echo "==> Updating apt and installing base packages"
apt-get update -y
apt-get install -y ca-certificates curl gnupg iptables-persistent netfilter-persistent

echo "==> Installing Docker Engine + Compose plugin (official repo, arm64)"
if ! command -v docker >/dev/null 2>&1; then
  install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
  chmod a+r /etc/apt/keyrings/docker.asc
  ARCH="$(dpkg --print-architecture)"
  CODENAME="$(. /etc/os-release && echo "$VERSION_CODENAME")"
  echo "deb [arch=${ARCH} signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu ${CODENAME} stable" \
    > /etc/apt/sources.list.d/docker.list
  apt-get update -y
  apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
  systemctl enable --now docker
else
  echo "Docker already installed, skipping."
fi

echo "==> Opening firewall for HTTP/HTTPS and persisting rules"
iptables -C INPUT -p tcp --dport 80  -j ACCEPT 2>/dev/null || iptables -I INPUT -p tcp --dport 80  -j ACCEPT
iptables -C INPUT -p tcp --dport 443 -j ACCEPT 2>/dev/null || iptables -I INPUT -p tcp --dport 443 -j ACCEPT
iptables -C INPUT -p tcp --dport 22  -j ACCEPT 2>/dev/null || iptables -I INPUT -p tcp --dport 22  -j ACCEPT
netfilter-persistent save
# Reminder: also open ingress 80/443 on the OCI VCN Security List / NSG -
# host firewall rules alone are not enough on OCI.
echo "    NOTE: open TCP 80 and 443 on the OCI VCN Security List/NSG too."

echo "==> Ensuring 2GB swap (Always Free A1 shapes ship with no swap)"
if ! swapon --show | grep -q '/swapfile'; then
  fallocate -l 2G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
  echo '/swapfile none swap sw 0 0' >> /etc/fstab
  # Favor reclaiming file cache over swapping app memory
  sysctl -w vm.swappiness=10
  echo 'vm.swappiness=10' > /etc/sysctl.d/99-swappiness.conf
else
  echo "Swap already configured, skipping."
fi

echo "==> Deploying application stack"
cd "$APP_DIR"
if [ ! -f .env ]; then
  echo "!! .env not found in $APP_DIR - copy .env.example to .env and fill in secrets first."
  exit 1
fi
docker compose pull --ignore-pull-failures || true
docker compose build
docker compose up -d

echo "==> Done. Check status with: docker compose ps && docker compose logs -f api"
