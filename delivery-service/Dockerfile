FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/delivery-service-1.0.0.jar app.jar
EXPOSE 8300
ENTRYPOINT ["java", "-jar", "app.jar"]
