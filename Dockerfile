FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY docker/wildfly/demo-mode.env docker/wildfly/demo-mode.env
COPY src src

RUN chmod +x gradlew && ./gradlew --no-daemon clean war

FROM flyway/flyway:11.10.5 AS flyway

FROM quay.io/wildfly/wildfly:39.0.1.Final-2-jdk21

USER root

RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main \
    /opt/jboss/wildfly/customization \
    /opt/bookstore/migrations \
    /opt/bookstore/dev-migrations && \
    chown -R jboss:root /opt/jboss/wildfly /opt/bookstore

ADD https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.7/postgresql-42.7.7.jar /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/postgresql-42.7.7.jar

COPY docker/wildfly/postgresql-module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/module.xml
COPY docker/wildfly/configure-datasource.cli /opt/jboss/wildfly/customization/configure-datasource.cli
COPY docker/wildfly/demo-mode.env /opt/jboss/wildfly/customization/demo-mode.env
COPY docker/wildfly/entrypoint.sh /opt/jboss/wildfly/customization/entrypoint.sh
COPY --from=flyway /flyway /opt/flyway
COPY src/main/resources/db/migration /opt/bookstore/migrations
COPY src/main/resources/db/dev /opt/bookstore/dev-migrations
COPY --from=build /workspace/build/libs/*.war /opt/jboss/wildfly/standalone/deployments/ROOT.war

RUN chmod 755 /opt/jboss/wildfly/customization/entrypoint.sh /opt/flyway/flyway && \
    chown -R jboss:root /opt/jboss/wildfly /opt/flyway /opt/bookstore

USER jboss

EXPOSE 8080 9990

ENTRYPOINT ["/opt/jboss/wildfly/customization/entrypoint.sh"]
