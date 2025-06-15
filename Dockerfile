# Build stage
FROM maven:3-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage (thay image lỗi bằng image ổn định)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/identity-service-0.0.1-SNAPSHOT.war identity-service.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "identity-service.war"]
