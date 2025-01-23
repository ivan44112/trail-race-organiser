FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV WEB_SOCKET_URL="ws://host.docker.internal:8081/ws"
ENV WEB_SOCKET_SUBSCRIPTION_TOPIC="/topic/commands"
ENV DATABASE_URL="jdbc:postgresql://postgres_db:5432/trail_race_organiser_db"
ENV DATABASE_USERNAME="intellexi"
ENV DATABASE_PASSWORD="password"