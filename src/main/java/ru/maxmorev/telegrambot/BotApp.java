package ru.maxmorev.telegrambot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maxmorev.telegrambot.core.QiwiPollingBot;
import ru.maxmorev.telegrambot.core.UserState;

public class BotApp {


    final static Logger logger = Logger.getLogger(BotApp.class);

    public static void main(String[] args) {


        try {

            ApiContextInitializer.init();

            TelegramBotsApi telegramBotsApi = createTelegramBotsApi();


            String proxyServer = "127.0.0.1";
            Integer proxyPort = 9050;

            //RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(false).build();
            //botOptions.setRequestConfig(requestConfig);
            // Set up Http proxy
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            botOptions.setProxyHost(proxyServer);
            botOptions.setProxyPort(proxyPort);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            try {
                logger.debug("STARTING BOT!");
                UserState state = new UserState();
                QiwiPollingBot bot =  new QiwiPollingBot(state, botOptions);
                telegramBotsApi.registerBot(bot);
                bot.init();
            } catch (TelegramApiException e) {
                e.printStackTrace();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi;

        telegramBotsApi = createLongPollingTelegramBotsApi();

        return telegramBotsApi;
    }

    /**
     * @brief Creates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     * @return TelegramBotsApi to register the bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }
}
