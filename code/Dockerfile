# Build stage: Compile the application using Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage: Run the application
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache ffmpeg python3 && \
    wget -O /usr/local/bin/yt-dlp https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

WORKDIR /app
COPY --from=build /app/target/webdl.jar /app/webdl.jar
ENTRYPOINT ["java", "-jar", "/app/webdl.jar"]
