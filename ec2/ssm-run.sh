#!/usr/bin/env bash
# Ejecuta un script en la EC2 por Systems Manager y espera el resultado.
# Uso: ssm-run.sh <instance_id> <region> <script_local> [argumentos...]
set -Eeuo pipefail

instance_id="${1:?instance id}"
region="${2:?region}"
host_script="${3:?script a ejecutar en la instancia}"
shift 3

params="$(mktemp)"
trap 'rm -f -- "$params"' EXIT
PYBIN=""
for candidate in python3 python; do
  if "$candidate" -c "import sys" >/dev/null 2>&1; then PYBIN="$candidate"; break; fi
done
[ -n "$PYBIN" ] || { echo "No se encontro python" >&2; exit 2; }

"$PYBIN" - "$host_script" "$params" "$@" <<'PY'
import sys, gzip, base64, json, shlex
script = open(sys.argv[1], "rb").read().replace(b"\r\n", b"\n")
b64 = base64.b64encode(gzip.compress(script)).decode()
args = " ".join(shlex.quote(a) for a in sys.argv[3:])
cmd = ("set -eu; "
       "for i in $(seq 1 40); do systemctl is-active --quiet docker && systemctl is-active --quiet nginx && break; sleep 5; done; "
       f"printf '%s' '{b64}' | base64 --decode | gzip --decompress > /tmp/ssm-deploy.sh; chmod 700 /tmp/ssm-deploy.sh; "
       f"bash /tmp/ssm-deploy.sh {args}; rc=$?; rm -f /tmp/ssm-deploy.sh; exit $rc")
json.dump({"commands": [cmd]}, open(sys.argv[2], "w"))
PY

# en Windows (git bash) la CLI necesita la ruta estilo C:/...
params_arg="$params"
if command -v cygpath >/dev/null 2>&1; then params_arg="$(cygpath -m "$params")"; fi

command_id="$(aws ssm send-command \
  --instance-ids "$instance_id" \
  --document-name AWS-RunShellScript \
  --comment "Deploy ForoNova desde GitHub Actions" \
  --timeout-seconds 900 \
  --parameters "file://$params_arg" \
  --region "$region" \
  --query Command.CommandId --output text)"
echo "Comando SSM: $command_id"

status=Pending
for attempt in $(seq 1 120); do
  status="$(aws ssm get-command-invocation --command-id "$command_id" --instance-id "$instance_id" \
    --region "$region" --query Status --output text 2>/dev/null || echo Pending)"
  case "$status" in Success|Failed|Cancelled|TimedOut) break ;; esac
  sleep 5
done

aws ssm get-command-invocation --command-id "$command_id" --instance-id "$instance_id" --region "$region" \
  --query "[StandardOutputContent,StandardErrorContent]" --output text | tail -40
if [ "$status" != "Success" ]; then
  echo "El despliegue termino con estado: $status" >&2
  exit 1
fi
echo "Despliegue en la EC2 terminado correctamente."
