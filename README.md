# webdl_bot

Telegram bot in Node.js for downloading social media videos using yt-dlp 

## Usage & info

webdl_bot accepts a video URL, downloads it, and sends it back to the user as a video message.

```shell
node index.js
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
docker build -t webdl .
docker run webdl
```

