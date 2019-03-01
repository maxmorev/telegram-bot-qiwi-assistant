package ru.maxmorev.telegrambot.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import ru.maxmorev.telegrambot.QIWIService;


public class QiwiPollingBot extends TelegramLongPollingBot {

	final static Logger logger = Logger.getLogger(QiwiPollingBot.class);


	protected static UserState state;
    protected static DefaultBotOptions options;

	protected static CallbackController callback;
	protected static MessageController messController;
	protected static CommandController commandController;

	public static final QIWIService qiwiService = new QIWIService("qiwiAccounts.json");

	
	//public DB database;

	public static QIWIService getAccountService() {
		return qiwiService;
	}

	// private static final String LOGTAG = "ZERO.BOT";
	private static final String CASH = "0.##";
	private static final DecimalFormat decimalFormat = new DecimalFormat(CASH);
	private static final List<Integer> authusers = new ArrayList<Integer>();
	private static final BotUserService userService = new BotUserService();
	
	protected  boolean checkSecure(Update update) {


		List<Integer> userUniq = new ArrayList<Integer>();
		
		userUniq.add(382324622);//YourId
		
		//users.
		//Message message = null;
		User user = null;
		Integer mesId = 0;
		if (update.hasInlineQuery()) {
			user = update.getInlineQuery().getFrom();
		}

		if (update.hasMessage() && update.getMessage().isUserMessage()) {
			user = update.getMessage().getFrom();
			mesId = update.getMessage().getMessageId();
		}
		
		if (update.hasCallbackQuery()) {
			user = update.getCallbackQuery().getFrom();
			mesId = update.getCallbackQuery().getMessage().getMessageId();
			
		}
		
		if(user!=null) { 
			logger.debug("Incoming user:"+user.getId()+"|"+user.getUserName()); 
			logger.debug("Incoming userMESSid:" + mesId );
			if(userUniq.contains(user.getId())) {
				authusers.add(user.getId());
				
				logger.debug("Message id:"+mesId);
				TelegramUserSettings telegramUser = userService.find(user.getId());
				if(telegramUser==null) {
					telegramUser = new TelegramUserSettings();
					logger.debug("SET usTELID >"+user.getId());
					telegramUser.setTelegramId(user.getId());
					telegramUser.setMessageId(0);
					userService.save(telegramUser);
					state.setUser(telegramUser);
					return true;
				}
				
				state.setUser(telegramUser);
				//TODO 
				/*
				 * AUTH METHOD
				if(telegramUser.getTipo().intValue()==2) {
					return false;
				}
				*/
				if(telegramUser.getMessageId()==0) {
					logger.debug("SET MESid >"+mesId);
					telegramUser.setMessageId(mesId);
					userService.save(telegramUser);
				}
				logger.debug("Accept user:"+user.getId()+"|"+user.getUserName());
				logger.debug(telegramUser);
				return true;
				
			}
			
		}
		
		return false;
	}
	
	

	public void onUpdateReceived(Update update) {
		
		try {
			
			//logger.debug("SECURE OFF!!!");
			if(!checkSecure(update)) {
				return;
			}
			logger.debug("onUpdateReceived");
			logger.debug("state:" + state.getCommand());
			
			//messController.setState(state);
			//callback.setState(state);
			//commandController.setState(state);
			
			if (update.hasInlineQuery()) {
				logger.debug("hasInlineQuery");
				logger.debug(update.getInlineQuery().getQuery());
				handleIncomingInlineQuery(update.getInlineQuery());
			}

			if (update.hasMessage() && update.getMessage().isUserMessage()) {

				messController.doMapping(update.getMessage(), update);
			}

			if (update.hasCallbackQuery()) {
				// Set variables
				String call_data = update.getCallbackQuery().getData();
				long message_id = update.getCallbackQuery().getMessage().getMessageId();
				long chat_id = update.getCallbackQuery().getMessage().getChatId();
				logger.debug(call_data);

				callback.doCallBack(update.getCallbackQuery(), update);
				/*
				 * if (!call_data.equals("update_msg_text")) { String answer =
				 * "Updated message text"; EditMessageText new_message = new EditMessageText()
				 * .setChatId(chat_id) .setMessageId((int)message_id) .setText(answer); try {
				 * execute(new_message); } catch (TelegramApiException e) { e.printStackTrace();
				 * } }
				 */

			}
		} catch (Exception e) {
			// BotLogger.error(LOGTAG, e);
		}
	}

	public String getBotUsername() {
		return "QIWI Assistant";
	}

    public void setOptions(DefaultBotOptions options) {
        this.options = options;
    }

    public QiwiPollingBot(UserState state, DefaultBotOptions options) {
        super(options);
        this.state = state;
        this.options = options;
    }

    public void init(){

        callback = new CallbackController(getState(), options);
        messController = new MessageController(getState(), options);
        commandController = new CommandController(getState(), options);

    }


	@Override
	public String getBotToken() {
		//TODO CHANGE TO YOUR TOKEN
		return "";

	}

	
	protected static String doFormat(double value) {

		return decimalFormat.format(value);
	}
	
	/**
	 * For an InlineQuery, results from RAE dictionariy are fetch and returned
	 * 
	 * @param inlineQuery
	 *            InlineQuery recieved
	 */
	private void handleIncomingInlineQuery(InlineQuery inlineQuery) {
		String query = inlineQuery.getQuery();
		logger.debug(query);
		return;

	}

	/**
	 * Converts resutls from RaeService to an answer to an inline query
	 * 
	 * @param inlineQuery
	 *            Original inline query
	 * @param results
	 *            Results from RAE service
	 * @return AnswerInlineQuery method to answer the query
	 */
	private static AnswerInlineQuery converteResultsToResponse(InlineQuery inlineQuery, String results) {
		AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
		// answerInlineQuery.setInlineQueryId(inlineQuery.getId());
		// answerInlineQuery.setCacheTime(CACHETIME);
		// answerInlineQuery.setResults();
		return answerInlineQuery;
	}

	public static UserState getState() {
		return state;
	}

	public void setState(UserState state) {
		this.state = state;
	}



	
	
	

}
