# Build stage: Compile the application using Maven
FROM maven:3.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage: Run the application
FROM alpine:latest

RUN apk update && \
    apk add --no-cache wget curl ffmpeg && \
    wget -O /usr/local/bin/yt-dlp https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp && \
    rm -rf /var/cache/apk/*

RUN apk add --no-cache openjdk21 python3 py3-pip

ENV YT_DLP=/usr/bin/yt-dlp PYTHONUNBUFFERED=1

WORKDIR /app
COPY --from=build /app/target/webdl.jar /app/webdl.jar
ENTRYPOINT ["java", "-jar", "/app/webdl.jar"]
