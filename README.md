# webdl

Telegram bot in Node.js for downloading social media videos using yt-dlp 

<img  src="https://raw.githubusercontent.com/SegoCode/webdl_bot/main/media/demo.gif">

## Usage & info

webdl_bot accepts a video URL, downloads it, and sends it back to the user as a video message.

```shell

```
Clone and run the project from source code.
```
git clone https://github.com/SegoCode/webdl_bot
cd webdl_bot\src
node index.js
```

## Docker
For Docker deployment, make sure to set up environment variables as per your Dockerfile.

```
mvn clean package
docker build -t webdl-image .
docker run -e BOT_TOKEN=your-bot-token --name webdl webdl-image
```

