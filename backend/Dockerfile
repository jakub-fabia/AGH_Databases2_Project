FROM maven:3-eclipse-temurin-24-alpine AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:24-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","app.jar"]