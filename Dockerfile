# Buoc 1: Build ma nguon voi Java 25
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Buoc 2: Chay ung dung voi Java 25 tren cong 80
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SERVER_PORT=80
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]