#!/bin/bash
set -euo pipefail

DB_URL="${DB_URL:-jdbc:postgresql://postgres:5432/bookstore}"
DB_USER="${DB_USER:-bookstore}"
DB_PASSWORD="${DB_PASSWORD:-bookstore}"
if [ -f /opt/jboss/wildfly/customization/demo-mode.env ]; then
  set -a
  . /opt/jboss/wildfly/customization/demo-mode.env
  set +a
fi
BOOKSTORE_DEMO_MODE="${BOOKSTORE_DEMO_MODE:-false}"
BOOKSTORE_DEMO_EMAIL="${BOOKSTORE_DEMO_EMAIL:-}"
BOOKSTORE_DEMO_PASSWORD="${BOOKSTORE_DEMO_PASSWORD:-}"
BOOKSTORE_DEMO_PASSWORD_HASH="${BOOKSTORE_DEMO_PASSWORD_HASH:-}"
FLYWAY_LOCATIONS="filesystem:/opt/bookstore/migrations"

if [ "${BOOKSTORE_DEMO_MODE}" = "true" ]; then
  FLYWAY_LOCATIONS="${FLYWAY_LOCATIONS},filesystem:/opt/bookstore/dev-migrations"
fi

echo "[bookstore] Configuring WildFly datasource"
/opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/wildfly/customization/configure-datasource.cli

echo "[bookstore] Running Flyway migrations"
/opt/flyway/flyway \
  -url="${DB_URL}" \
  -user="${DB_USER}" \
  -password="${DB_PASSWORD}" \
  -connectRetries=60 \
  -baselineOnMigrate=true \
  -locations="${FLYWAY_LOCATIONS}" \
  -placeholders.demoEmail="${BOOKSTORE_DEMO_EMAIL}" \
  -placeholders.demoPasswordHash="${BOOKSTORE_DEMO_PASSWORD_HASH}" \
  migrate

echo "[bookstore] Starting WildFly"
exec /opt/jboss/wildfly/bin/standalone.sh -c standalone-full.xml -b 0.0.0.0 -bmanagement 0.0.0.0
