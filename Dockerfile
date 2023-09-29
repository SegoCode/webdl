# Imagen base de Node.js
FROM node:16

# Establecer el directorio de trabajo en el contenedor
WORKDIR /usr/src/app

# Copiar package.json y package-lock.json al directorio de trabajo
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar el código fuente al contenedor
COPY . .

# Configurar la variable de entorno para yt-dlp
ENV YT_DLP_PATH=/usr/src/app/yt-dlp/yt-dlp
RUN chmod +x /usr/src/app/yt-dlp/yt-dlp

# Cambiar el directorio de trabajo al subdirectorio src
WORKDIR /usr/src/app/src

# Comando para iniciar la aplicación
CMD [ "node", "index.js" ]
