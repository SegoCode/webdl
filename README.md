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

- Non-blocking message queue processing with virtual threads

- Dynamic interaction with Telegram messages (send, delete, edit)

- Web panel with usage statistics on port 8080

- Automatic retry on download failures

## Quick Start & Information

Webdl accepts a video URL, downloads it using [yt-dlp](https://github.com/yt-dlp/yt-dlp), and sends it back to the user as a video message.

### From source

```
git clone https://github.com/SegoCode/webdl
cd webdl/code
mvn clean package -DskipTests
java -jar target/webdl.jar
```

### Docker

```
cd webdl/code
mvn clean package -DskipTests
docker build -t webdl-image .
docker run -d \
  --name webdl \
  --restart unless-stopped \
  -e BOT_TOKEN=your-bot-token \
  -p 8080:8080 \
  -v /mnt/drive/data/webdl:/downloads \
  webdl-image
```

### Project structure

```
code/src/main/java/org/segocode/webdl/
├── Main.java                          # Entry point
├── bot/
│   ├── Webdlbot.java                  # Telegram long-polling bot
│   ├── constants/Messages.java        # User-facing message strings
│   ├── model/{User,DataRootContainer}.java  # EclipseStore persistence
│   ├── service/{MessageService,VideoService}.java
│   └── util/MessageUtil.java
├── panel/
│   ├── PanelApplication.java          # Javalin web server bootstrap
│   └── AdminController.java           # Admin panel route handler
└── system/
    ├── command/CommandExecutor.java   # yt-dlp subprocess with retry
    └── util/FileUtil.java
```



---
<p align="center"><a href="https://github.com/SegoCode/webdl/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=SegoCode/webdl" />
</a></p>
