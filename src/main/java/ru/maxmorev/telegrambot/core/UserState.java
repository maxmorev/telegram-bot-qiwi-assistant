package ru.maxmorev.telegrambot.core;

//import zero.bot.one.db.nano.ZeroUser;

public class UserState {
	private String command;
	private Integer messageId;
	private TelegramUserSettings user;
	
	public UserState() {
		super();
		command = "";
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public boolean isEmpty() {
		return command.isEmpty();
	}

	public Integer getMessageId() {
		return user.getMessageId();
		//return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.user.setMessageId(messageId);
		this.messageId = messageId;
	}

	public TelegramUserSettings getUser() {
		// TODO Auto-generated method stub
		return user;
	}

	public void setUser(TelegramUserSettings user) {
		this.user = user;
	}
	
	
}
