FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/user-service-1.0.0.jar app.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "app.jar"]
