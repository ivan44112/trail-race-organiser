FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV JWT_SECRET_KEY="mYv3nX82gZqI2f8jU+Lq5TzS6Yw/X1aBc3M5R0t0pC4="
ENV JWT_EXPIRATION_TIME="3600000"
ENV WEB_SOCKET_URL="ws://host.docker.internal:8082/ws"
ENV WEB_SOCKET_SUBSCRIPTION_TOPIC="/topic/responses"
ENV DATABASE_URL="jdbc:postgresql://postgres_db:5432/trail_race_organiser_db"
ENV DATABASE_USERNAME="intellexi"
ENV DATABASE_PASSWORD="password"

