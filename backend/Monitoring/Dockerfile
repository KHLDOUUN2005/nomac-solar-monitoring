# --- Build stage ---
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
RUN chmod +x mvnw
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests -B

# --- Run stage ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]