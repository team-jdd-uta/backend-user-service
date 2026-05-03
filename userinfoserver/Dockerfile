FROM gradle:8.14.4-jdk21-alpine AS build

WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle --no-daemon bootJar -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache ca-certificates wget && \
    wget -O /tmp/aws-rds-global-bundle.pem https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem && \
    awk 'BEGIN { cert = 0 } /BEGIN CERTIFICATE/ { cert++ } { print > "/tmp/aws-rds-ca-" cert ".pem" }' /tmp/aws-rds-global-bundle.pem && \
    for cert_file in /tmp/aws-rds-ca-*.pem; do \
      keytool -importcert -noprompt -storepass changeit -cacerts -alias "aws-rds-$(basename "$cert_file" .pem)" -file "$cert_file"; \
    done && \
    rm -f /tmp/aws-rds-global-bundle.pem /tmp/aws-rds-ca-*.pem

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
