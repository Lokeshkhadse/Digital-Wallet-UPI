FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/Eureka-Server.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]

