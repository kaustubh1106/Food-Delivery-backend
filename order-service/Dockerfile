FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/order-service-1.0.0.jar app.jar
EXPOSE 8400
ENTRYPOINT ["java", "-jar", "app.jar"]
