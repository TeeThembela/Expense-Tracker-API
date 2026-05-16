FROM maven:3.9.9-eclipse-temurin-22 AS builder
WORKDIR /expense-tracker-api-app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jre
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser
WORKDIR /app
COPY --from=builder --chown=appuser:appgroup ./expense-tracker-api-app/target/expense-tracker-api.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]