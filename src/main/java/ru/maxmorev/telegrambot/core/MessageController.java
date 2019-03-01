package ru.maxmorev.telegrambot.core;


import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageController extends QiwiPollingBot {

	public MessageController(UserState state, DefaultBotOptions options ) {
		super(state, options);
		logger.debug("MessageController CREATED");
		// TODO Auto-generated constructor stub
	}

	public SendMessage doMapping(Message message, Update update) {
		logger.debug("doMapping state:" + getState().getCommand());
		logger.debug(message.getText());
		SendMessage sendMessage = new SendMessage(); // Create a message object object
		String text = message.getText();
		long chatId = message.getChatId();
		sendMessage.setChatId(chatId);
		sendMessage.setText(text);
		if (message.isCommand()) {
			logger.debug("isCommand");
			if (text.startsWith("/")) {
				commandController.commandMapper(sendMessage, update);
			}

			// commandHendler
		} else {
			if (!getState().isEmpty()) {
				callback.processUpdate(sendMessage, update);
			} else {
				sendMessage.setText("Wasup bitch?");
				try {
					execute(sendMessage); // Sending our message object to user
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}

		}

		return sendMessage;
	}

	public EditMessageText callBack(Message message, CallbackQuery callback) {
		// callBack handler
		return null;

	}

}
