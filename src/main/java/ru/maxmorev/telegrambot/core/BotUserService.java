package ru.maxmorev.telegrambot.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


public class BotUserService {

	final static Logger logger = Logger.getLogger(BotUserService.class);
	
	Users users;
	
	String file = "users";
	
	public BotUserService() {
		super();
		loadAccounts();
	}

	
	private Users loadAccounts() {
		logger.debug("loadAccounts");
		ObjectMapper mapper = new ObjectMapper();
		// JSON from file to Object
		Users accounts = new Users();
		try {
			users = mapper.readValue(new File(file), Users.class);
			System.out.println("ACCOUNTS LOADEDE < "+users.getAccounts().size());
		} catch (IOException e) {
			this.users = new Users();
		}
		return accounts;
	}

	private boolean tryUpdateFile() {
		System.out.println("@tryUpdateFile");
		ObjectMapper mapper = new ObjectMapper();
		// Convert object to JSON string and save
		try {
			System.out.println("SAVEING USERS > " + this.users.getAccounts().size());
			System.out.println(this.users.getAccounts().toString());
			mapper.writeValue(new File(file), this.users);
			//FileUtil ff = new FileUtil();
			//ff.saveFile("cache.tmp", accounts.toString());
			
		} catch (Exception e) {
			System.out.println("faild save file. Sucks. I'LL be alive till power off..=(");
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public TelegramUserSettings find(Integer telegramId) {
		System.out.println("try find user < "+telegramId);

		Optional<TelegramUserSettings> findUser = users.getAccounts().stream().filter(user->user.getTelegramId().equals(telegramId)).findFirst();
		if(findUser.isPresent()){
			return findUser.get();
		}
		return null;
		
	}
	
	public void save(TelegramUserSettings user) {
		System.out.println("BotUserService -> save " + user.getMessageId());
		if(users.getAccounts().contains(user)){
			users.getAccounts().remove(user);
			users.getAccounts().add(user);
		}else{
			users.getAccounts().add(user);
		}
		tryUpdateFile();
		
	}
	
	

}
