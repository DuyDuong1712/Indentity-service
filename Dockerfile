FROM maven:3-openjdk-11-slim AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

#Run stage

FROM openjdk:11-jre-slim
WORKDIR /app

COPY --from=build /app/target/identity-service-0.0.1-SNAPSHOT.war  identity-service.war
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "identity-service.war"]