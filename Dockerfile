FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENV DB_HOST=localhost \
    DB_PORT=1521 \
    DB_NAME=XEPDB1 \
    DB_USER=system \
    DB_PASS=orclvk3

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
