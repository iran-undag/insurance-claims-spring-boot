FROM eclipse-temurin:17.0.20_8-jre-jammy

WORKDIR /app

RUN groupadd --system app && useradd --system --gid app --home-dir /app app

COPY --chown=app:app target/insurance-claims-spring-boot-0.0.1-SNAPSHOT.jar app.jar

USER app

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
