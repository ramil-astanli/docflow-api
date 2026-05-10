# 1. Build stage
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Run stage
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir ./uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]