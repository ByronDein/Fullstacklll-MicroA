#!/usr/bin/env bash
# Se ejecuta DENTRO de la EC2 (por Systems Manager). Actualiza UN contenedor con la ultima imagen de ECR.
# Uso: deploy-component.sh <componente> <region> <registry> [secret_arn] [wallet_bucket] [wallet_key]
#   componente: usuarios-service | foro-service | foro-frontend
set -Eeuo pipefail

component="${1:?componente}"
region="${2:?region}"
registry="${3:?registry ECR}"
secret_arn="${4:-}"
wallet_bucket="${5:-}"
wallet_key="${6:-}"
image="$registry/$component:latest"
wallet_dir=/opt/foro/wallet
env_file="$(mktemp /tmp/foro-env.XXXXXX)"
trap 'rm -f -- "$env_file"' EXIT

echo "== Descargando $image"
aws ecr get-login-password --region "$region" | docker login --username AWS --password-stdin "$registry" >/dev/null
docker pull "$image"
docker logout "$registry" >/dev/null 2>&1 || true
docker network create foro-net >/dev/null 2>&1 || true

prepare_oracle() {
  if [ -z "$secret_arn" ] || [ -z "$wallet_bucket" ] || [ -z "$wallet_key" ]; then
    echo "Faltan datos de Oracle (secreto, bucket o clave del wallet)" >&2
    exit 2
  fi
  echo "== Wallet de Oracle desde S3"
  mkdir -p "$wallet_dir"
  aws s3 cp "s3://$wallet_bucket/$wallet_key" /tmp/wallet.zip --region "$region" --only-show-errors
  python3 -m zipfile -e /tmp/wallet.zip "$wallet_dir"
  rm -f /tmp/wallet.zip
  chmod -R a+rX "$wallet_dir"
  echo "== Credenciales de Oracle desde Secrets Manager"
  aws secretsmanager get-secret-value --secret-id "$secret_arn" --region "$region" --query SecretString --output text \
    | python3 -c '
import json, sys
secret = json.load(sys.stdin)
with open(sys.argv[1], "w", encoding="utf-8") as out:
    for key in ("DB_URL", "DB_USERNAME", "DB_PASSWORD"):
        value = secret.get(key)
        if not isinstance(value, str) or "\n" in value or "\r" in value:
            raise SystemExit("El secreto no tiene un valor valido para " + key)
        out.write(key + "=" + value + "\n")
    out.write("TNS_ADMIN=/app/wallet\n")
' "$env_file"
  chmod 600 "$env_file"
}

docker rm -f "$component" >/dev/null 2>&1 || true
case "$component" in
  foro-frontend)
    docker run -d --name foro-frontend --network foro-net --restart unless-stopped \
      --publish 127.0.0.1:4000:80 "$image" >/dev/null
    health="http://127.0.0.1/"
    ;;
  usuarios-service)
    prepare_oracle
    docker run -d --name usuarios-service --network foro-net --restart unless-stopped \
      --publish 127.0.0.1:8081:8081 --env-file "$env_file" -v "$wallet_dir":/app/wallet:ro "$image" >/dev/null
    health="http://127.0.0.1:8081/roles"
    ;;
  foro-service)
    prepare_oracle
    docker run -d --name foro-service --network foro-net --restart unless-stopped \
      --publish 127.0.0.1:8082:8082 --env-file "$env_file" \
      -e USUARIOS_SERVICE_URL=http://usuarios-service:8081 \
      -v "$wallet_dir":/app/wallet:ro "$image" >/dev/null
    health="http://127.0.0.1:8082/categorias"
    ;;
  *)
    echo "Componente desconocido: $component" >&2
    exit 2
    ;;
esac
rm -f -- "$env_file"

echo "== Esperando a que responda $health (maximo 5 minutos)"
for i in $(seq 1 60); do
  if curl -fsS --max-time 5 "$health" >/dev/null 2>&1; then
    echo "$component actualizado y respondiendo."
    exit 0
  fi
  sleep 5
done
echo "$component no respondio a tiempo. Ultimos logs:" >&2
docker logs --tail 40 "$component" >&2 || true
exit 1
