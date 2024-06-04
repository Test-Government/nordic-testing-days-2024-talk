# First stage
FROM maven:3.8.5-openjdk-17-slim AS builder
COPY ./pom.xml pom.xml
COPY ./src src/
RUN mvn clean package

# Second stage
FROM eclipse-temurin:17.0.4.1_1-jre
ARG JAR_FILE=target/petclinic*.jar
COPY --from=builder ${JAR_FILE} petclinic-testdata.jar

USER www-data

ENTRYPOINT ["java", "-jar", "petclinic-testdata.jar"]
