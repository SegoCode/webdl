# webdl

<img  src="https://raw.githubusercontent.com/SegoCode/webdl_bot/main/media/demo2.gif">
<img  src="https://raw.githubusercontent.com/SegoCode/webdl_bot/main/media/demoPanel.png">

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

- Panel with usage statistics 

## Quick Start & Information

Webdl accepts a video URL, downloads it using [yt-dlp](https://github.com/yt-dlp/yt-dlp), and sends it back to the user as a video message.

Clone and run the project from source code.
```
git clone https://github.com/SegoCode/webdl
cd webdl\code
mvn clean package
java -jar target/webdl.jar
```

For Docker deployment, make sure to set up environment variables.
Use a temp volume for the download, it will delete after send.
```
mvn clean package
docker build -t webdl-image .
docker run -e BOT_TOKEN=your-bot-token -p 8080:8080 -v /mnt/drive/data/webdl:/downloads --name webdl webdl-image
```



---
<p align="center"><a href="https://github.com/SegoCode/webdl/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=SegoCode/webdl" />
</a></p>
