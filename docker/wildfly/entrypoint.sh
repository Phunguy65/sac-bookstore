#!/bin/bash
set -euo pipefail

DB_URL="${DB_URL:-jdbc:postgresql://postgres:5432/bookstore}"
DB_USER="${DB_USER:-bookstore}"
DB_PASSWORD="${DB_PASSWORD:-bookstore}"

echo "[bookstore] Configuring WildFly datasource"
/opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/wildfly/customization/configure-datasource.cli

echo "[bookstore] Running Flyway migrations"
/opt/flyway/flyway \
  -url="${DB_URL}" \
  -user="${DB_USER}" \
  -password="${DB_PASSWORD}" \
  -connectRetries=60 \
  -baselineOnMigrate=true \
  -locations=filesystem:/opt/bookstore/migrations \
  migrate

echo "[bookstore] Starting WildFly"
exec /opt/jboss/wildfly/bin/standalone.sh -c standalone-full.xml -b 0.0.0.0 -bmanagement 0.0.0.0
