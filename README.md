<h1 align="center"><img width="250" height="150" src="https://github.com/SegoCode/webdl/blob/develop/media/logo.png"></h1>

<h3 align="center"><img src="media/demo2.gif"></h3>

<p align="center">
  <a href="#about">About</a> •
  <a href="#features">Features</a> •
  <a href="#quick-start--information">Quick Start & Information</a>
</p>

## About
[![Top language](https://img.shields.io/github/languages/top/SegoCode/webdl?style=flat-square)](https://github.com/SegoCode/webdl)
[![Repository size](https://img.shields.io/github/repo-size/SegoCode/webdl?style=flat-square&label=repo%20size)](https://github.com/SegoCode/webdl)
[![Commit activity per year](https://img.shields.io/github/commit-activity/y/SegoCode/webdl?style=flat-square&label=commits)](https://github.com/SegoCode/webdl/graphs/commit-activity)
[![Licencia: PolyForm Noncommercial + GNU AGPL-3.0](https://img.shields.io/badge/License-PolyForm%20Noncommercial%20%2B%20GNU%20AGPL--3.0-blue?style=flat-square)](https://github.com/SegoCode/webdl/blob/main/LICENSE)
[![Bitcoin BTC](https://img.shields.io/badge/buy_me_a_coffee-BTC-F7931A?style=flat-square&logo=bitcoin&logoColor=white)](https://github.com/SegoCode/SegoCode/discussions/2)


Telegram bot in Java for downloading social media videos using [yt-dlp](https://github.com/yt-dlp/yt-dlp). Send a video URL, get the file back as a Telegram video message.

## Features

- Non-blocking message queue processing with virtual threads

- Dynamic interaction with Telegram messages (send and delete)

- Automatic retry on download failures

## Quick Start & Information

Requires Java 21, Maven, and [yt-dlp](https://github.com/yt-dlp/yt-dlp) available on `PATH` and set `BOT_TOKEN` to your Telegram bot token.

### From source

```shell
git clone https://github.com/SegoCode/webdl
cd webdl/code
mvn clean package -DskipTests
java -jar target/webdl.jar
```

### Docker

```shell
cd webdl/code
mvn clean package -DskipTests
docker build -t webdl-image .
docker run -d \
  --name webdl \
  --restart unless-stopped \
  -e BOT_TOKEN=your-bot-token \
  webdl-image
```

---
<p align="center"><a href="https://github.com/SegoCode/webdl/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=SegoCode/webdl" />
</a></p>
