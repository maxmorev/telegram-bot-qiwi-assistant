package ru.maxmorev.telegrambot.core;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


public class CommandController extends QiwiPollingBot{
	//CommandController.private Command commands;
	

	
	
	public void commandMapper(SendMessage sendMessage , Update update) {
		String command = sendMessage.getText();
		Command c = Command.getCommand(command);
		logger.debug("commandMapper: " +c);
		switch(c) {
        case QIWICOMMAND:
        	getState().setCommand(callback.INITIALVIEW);
        	callback.processUpdate(sendMessage, update);
            //this.processInitialView(sendMessage);
            break;
        case START:
        	logger.debug(">START");
        	getState().setCommand(callback.INITIALVIEW);
        	TelegramUserSettings user = getState().getUser();
        	user.setMessageId(0);
        	callback.processUpdate(sendMessage, update);

        	
            break;
		
        default:
            //sendMessage.setText("unknown_command");
            //try {execute(sendMessage);} catch (TelegramApiException e) {e.printStackTrace();}
		}
		return;
	}
	
	public CommandController(UserState state, DefaultBotOptions options ) {
        super(state, options);
		logger.debug("CREATED CommandController");
	}
		

}
