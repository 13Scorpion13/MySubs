FROM openjdk:17-jdk-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

COPY target/java-app-0.0.1-SNAPSHOT.jar /app/java-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "java-app.jar"]