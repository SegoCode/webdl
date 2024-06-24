# Use the official Alpine as a parent image
FROM alpine:latest

# Install the necessary packages
RUN apk update && \
    apk add --no-cache wget curl ffmpeg && \
    wget -O /usr/local/bin/yt-dlp https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp && \
    rm -rf /var/cache/apk/*

# Install the necessary packages
RUN apk add --no-cache openjdk21 python3 py3-pip

# Set the YT_DLP environment variable
ENV YT_DLP=/usr/local/bin/yt-dlp

# Create the application directory
RUN mkdir -p /app/downloads

# Copy all necessary files to the app directory
COPY . /app

# Run the application
ENTRYPOINT ["java","-jar","/app/target/webdl.jar"]