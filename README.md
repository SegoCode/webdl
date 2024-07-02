# webdl

<img  src="https://raw.githubusercontent.com/SegoCode/webdl_bot/main/media/demo2.gif">

<p align="center">
  <a href="#about">About</a> •
  <a href="#features">Features</a> •
  <a href="#quick-start--information">Quick Start & Information</a>
</p>


## About
Telegram bot in Java for downloading social media videos using yt-dlp

## Features

- Non-blocking message queue processing

- Dynamic interaction with messages

- No complex architecture, simple and easy for everyone

- The fewest possible dependencies


## Quick Start & Information

webdl accepts a video URL, downloads it, and sends it back to the user as a video message.

Clone and run the project from source code.
```
git clone https://github.com/SegoCode/webdl
cd webdl\code
mvn clean package
java -jar webdl.jar
```

For Docker deployment, make sure to set up environment variables.

```
mvn clean package
docker build -t webdl-image .
docker run -e BOT_TOKEN=your-bot-token -v /mnt/drive/data/webdl:/downloads --name webdl webdl-image
```

