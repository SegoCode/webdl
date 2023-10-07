require('dotenv').config();
const { Telegraf } = require('telegraf');
const validUrl = require('valid-url');
const { exec } = require('child_process');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

const bot = new Telegraf(process.env.BOT_TOKEN);

const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

bot.command('start', (ctx) => {
    ctx.reply('👋 ¡Hola! Iniciado el bot de descarga de videos 🎥. Envía un enlace válido 🌐 para iniciar la descarga. 📥');
});


bot.on('text', async (ctx) => {
    let texto = ctx.message.text;

    if (validUrl.isUri(texto)) {
        await delay(1000);
        const loadingMsg = await ctx.reply('Peticion de descarga lanzada 🔄', { reply_to_message_id: ctx.message.message_id });
        await ctx.replyWithChatAction('upload_video');

        const randomID = uuidv4();
        const outputFolder = 'downloads';
        const outputFile = path.join(outputFolder, `${randomID}.mp4`);

        console.log(`Creando carpeta y preparando para descargar: ${outputFile}`);

        if (!fs.existsSync(outputFolder)) {
            fs.mkdirSync(outputFolder);
        }

        const command = `${process.env.YT_DLP_PATH} -o "${outputFile}" "${texto}"`;

        exec(command, async (error, stdout, stderr) => {
            if (error) {
                console.error(`Error en la descarga: ${error}`);
                return;
            }

            console.log(`Archivo descargado: ${outputFile}`);

            const stats = fs.statSync(outputFile);
            const fileSizeInBytes = stats.size;
            const fileSizeInMegabytes = fileSizeInBytes / (1024*1024);

            if (fileSizeInMegabytes <= 50) {
                await delay(1000);
                await ctx.replyWithVideo({ source: fs.createReadStream(outputFile) }, { reply_to_message_id: ctx.message.message_id });
                await ctx.telegram.deleteMessage(ctx.chat.id, loadingMsg.message_id);
                console.log(`Archivo enviado y mensaje eliminado: ${outputFile}`);
            } else {
                await ctx.reply('🚫 El archivo es demasiado grande para enviar 📦 (más de 50Mb).', { reply_to_message_id: ctx.message.message_id });
                console.log(`Archivo demasiado grande para enviar: ${outputFile}`);
            }

            fs.unlinkSync(outputFile);
            console.log(`Archivo eliminado: ${outputFile}`);
        });
    } else {
        await ctx.reply('🚫 Oops! Ese no parece ser un enlace válido.');
    }
});

bot.launch();
