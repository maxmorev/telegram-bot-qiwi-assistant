package ru.maxmorev.telegrambot.core;

public enum Command {
	
	QIWICOMMAND, START, DEFAULTCOMMAND;
	
	public static Command getCommand(String thiscommand) {
		String res = thiscommand.replace('/', ' ').trim();
		if(res.equals("qiwi")) {
			return QIWICOMMAND;
		}
		if(res.equals("start")) {
			return START;
		}
		return DEFAULTCOMMAND;
	}
	

}
