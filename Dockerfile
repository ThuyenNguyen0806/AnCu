FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/quan-ly-nha-o-1.0.0.jar app.jar
ENV SERVER_ADDRESS=0.0.0.0
ENV PORT=10000
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:InitialRAMPercentage=15 -XX:+UseSerialGC"
EXPOSE 10000
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
