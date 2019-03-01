package ru.maxmorev.telegrambot.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Users {
	
	private Collection<TelegramUserSettings> accounts;
	
	public Users() {
		super();
		accounts = new HashSet<>();
	}

	public Collection<TelegramUserSettings> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<TelegramUserSettings> accounts) {
		this.accounts = accounts;
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
	

}
