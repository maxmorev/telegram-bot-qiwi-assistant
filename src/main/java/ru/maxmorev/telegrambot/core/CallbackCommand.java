package ru.maxmorev.telegrambot.core;

public enum CallbackCommand {
	
	QIWICALLBACK, DEFCALLBACK;
	
	public static CallbackCommand getCommand(String name) {
		if( name.equals("@qiwi")) {
			return QIWICALLBACK;
		}
		return DEFCALLBACK;
	}

}
