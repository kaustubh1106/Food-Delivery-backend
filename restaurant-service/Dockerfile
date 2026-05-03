FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/restaurant-service-1.0.0.jar app.jar
EXPOSE 8200
ENTRYPOINT ["java", "-jar", "app.jar"]
