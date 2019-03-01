package ru.maxmorev.telegrambot.core;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TelegramUserSettings {
	private Integer telegramId;
	private Integer messageId;
	
	public Integer getTelegramId() {
		return telegramId;
	}
	public void setTelegramId(Integer telegramId) {
		this.telegramId = telegramId;
	}
	public Integer getMessageId() {
		return messageId;
	}
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(this);
		}catch(Exception e) {
			
		}
		return jsonStr;
	
	}

	@Override
	public int hashCode() {
		return telegramId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TelegramUserSettings other = (TelegramUserSettings) obj;
		if(this.telegramId.equals(other.telegramId)) { return true;} else {return false;}

	}
}
