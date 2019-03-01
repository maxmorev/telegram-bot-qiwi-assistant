package ru.maxmorev.telegrambot.core;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maxmorev.payment.qiwi.QIWI;
import ru.maxmorev.payment.qiwi.response.Payment;
import ru.maxmorev.payment.qiwi.response.QiwiResponse;
import ru.maxmorev.payment.qiwi.response.Transaction;
import ru.maxmorev.telegrambot.QIWISettings;
import ru.maxmorev.utill.NumberString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CallbackController extends QiwiPollingBot {


	String callADD = "@qiwi:ADD:{SETUP}"; // "@qiwi:{action}:{wallet}:{token}"
	public String INITIALVIEW = "@qiwi:ACCOUNTS:START";
	String callWITHDRAWALLSETUP = "@qiwi:" + CallbackAction.WITHDRAWALLSETUP + ":{SETUP}";
	String callCOMMENTSEARCHSETUP = "@qiwi:" + CallbackAction.COMMENTSEARCH + ":{SETUP}";

	String callWITHDRAWALLHIST = "@qiwi:" + CallbackAction.WITHDRAWALLHIST + ":{SETUP}";

	double transferLimit = 15000;
	double comission = 0.02;
	
	
	
	public CallbackController(UserState state, DefaultBotOptions options ) {
		super(state, options);
		logger.debug("CREATED CallbackController");
		return;
	}
	

	public void doEditMessage(SendMessage sendMessage, Update update) {
		logger.debug("[] doEditMessage");
		logger.debug(sendMessage.getText());
		Integer mid2 = update.getCallbackQuery().getMessage().getMessageId();
		EditMessageText editMessage = new EditMessageText();
		editMessage.setMessageId(getState().getMessageId());
		editMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
		String title = EmojiParser.parseToUnicode(sendMessage.getText());
		editMessage.setText(title);

		logger.debug("EDIT MESSAGE state.id=" + getState().getMessageId());
		logger.debug("EDIT MESSAGE update.cb.id=" + mid2);
		InlineKeyboardMarkup markupInline = (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
		editMessage.setReplyMarkup(markupInline);

		try {
            execute(editMessage);
			//editMessageText(editMessage);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getState().setMessageId(mid2);
			try {
				editMessage.setMessageId( mid2 );
                execute(editMessage);
				//dddddeditMessageText(editMessage);
			}catch(Exception ex) {
				e.printStackTrace();
				doSendByExecute(sendMessage, update);
			}
		}
	}

	public void doEditMessageSmall(SendMessage sendMessage) {
		logger.debug("[] doEditMessage");
		logger.debug(sendMessage.getText());
		// Integer mid2 = update.getCallbackQuery().getMessage().getMessageId();
		EditMessageText editMessage = new EditMessageText();
		// .editMessage.InlineKeyboardMarkup markupInlinep = (InlineKeyboardMarkup)
		// sendMessage.getReplyMarkup();
		// editMessage.setInlineMessageId(String.valueOf(update.getCallbackQuery().getMessage().getMessageId()));
		editMessage.setMessageId(getState().getMessageId());
		editMessage.setChatId(sendMessage.getChatId());
		String title = EmojiParser.parseToUnicode(sendMessage.getText());
		editMessage.setText(title);
		logger.debug("EDIT MESSAGE state.id=" + getState().getMessageId());
		try {
			execute(editMessage);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doSendByExecute(SendMessage sendMessage, Update update) {
		logger.debug("doSendByExecute");
		try {
			String resAns = EmojiParser.parseToUnicode(sendMessage.getText());
			if (resAns.isEmpty()) {
				resAns = "text";
			}
			sendMessage.setText(resAns);
			// this.editMessageText(editMessageText, sentCallback);
			execute(sendMessage); // Sending our message object to user
			//getState().setMessageId( update.getCallbackQuery().getMessage().getMessageId() );
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void processUpdate(SendMessage sendMessage, Update update) {
		logger.debug("processUpdate state:" + getState().getCommand());
		String callData = getState().getCommand();

		callData = callData.replace("{SETUP}", sendMessage.getText().replaceAll(" ", ":").trim());
		logger.debug("processUpdate");
		logger.debug(callData);

		CallbackCommand calCom = CallbackCommand.getCommand(this.getControllerName(callData));
		switch (calCom) {
		case QIWICALLBACK:
			String[] args = callData.split(":");
			String action = args[1];
			logger.debug("ACTION on UPDATE: " + action);

			List<String> argslist = new ArrayList<String>();
			int i = 2;
			while (i < args.length) {
				argslist.add(args[i]);
				i += 1;
			}
			logger.debug("ARG LIST: " + argslist.size());

			CallbackAction actionCB = CallbackAction.getAction(action);
			logger.debug(actionCB + argslist.toString());
			switch (actionCB) {

			case ADD:
				logger.debug("OK IM IN ADD ACTION");
				if (argslist.size() < 2) {
					sendMessage.setText(
							"Пришлите номер кошелька, для которого получен токен ( с международным префиксом, но без + ) пробел токен.\n"
									+ "Пример: 79112223344 YUu2qw048gtdsvlk3iu");
					// state. = callADD;
					getState().setCommand(callADD);
					this.doSendByExecute(sendMessage, update);
					return;

				}
				this.doAdd(argslist, sendMessage);
				getState().setCommand("");
				getState().getUser().setMessageId(0);
				this.defaultMenu(sendMessage);
				this.doSendByExecute(sendMessage, update);
				//processInitialView(sendMessage, update);
				return;
				//break;

			case WITHDRAWALLSETUP:
				logger.debug("processUpdate > OK IM IN WITHDRAWALLSETUP ACTION");
				if (argslist.size() < 2) {
					sendMessage.setText(":alien: Пришлите номер кошелька получателя пробел сумма.\n"
							+ "Пример: 79112223344 500\n" + "максимальная сумма: "
							+ String.valueOf(transferLimit));
					getState().setCommand(callWITHDRAWALLSETUP);
					this.doSendByExecute(sendMessage, update);
					return;
				}

				// transfer process
				if (doTransferCheck(argslist, sendMessage)) {
					askBeforeDo(callData, sendMessage);
					logger.debug("after askBeforeDo > " + this.getState().getMessageId());
					this.getState().getUser().setMessageId(0);
					this.doSendByExecute(sendMessage, update);

					return;
				}
				logger.debug("Error in doTransferCheck");
				this.defaultMenu(sendMessage);
				getState().setCommand("");
				getState().getUser().setMessageId(0);
				this.doSendByExecute(sendMessage, update);
				//processInitialView(sendMessage, update);
				return;
				
			case WITHDRAWALLHIST:
				logger.debug("> processUpdate > OK IM IN WITHDRAWALLHIST ACTION");
				if (argslist.size() < 1) {
					sendMessage.setText(":alien: Пришлите номер кошелька получателя\n" + "Пример: 79112223344\n");
					getState().setCommand(callWITHDRAWALLHIST);
					logger.debug("Set command: " + callWITHDRAWALLHIST);
					defaultSmallMenu(sendMessage);
					doEditMessage(sendMessage, update);
					return;
				}
				logger.debug("RECIEVER > " + argslist.get(0));
				this.getState().getUser().setMessageId(0);
				getState().setCommand("");
				doTransferHist(argslist, sendMessage);
				defaultMenu(sendMessage);
				this.doSendByExecute(sendMessage, update);
				processInitialView(sendMessage, update);
				return;
				
				
			case ACCOUNTS:
				logger.debug("OK IM IN UPDATE ACCOUNTS ACTION");
				getState().setCommand("");
				processInitialView(sendMessage, update);
				//processInitialView(sendMessage, update);
				break;
			
			case ACCOUNTSARCHIVE:
				processInitialView(sendMessage, update);
				getState().setCommand("");
				break;

			case COMMENTSEARCH:
				logger.debug("OK IM IN UPDATE COMMENTSEARCH ACTION");
				if (argslist.size() < 2) {
					sendMessage.setText(":alien: Пришлите номер кошелька пробел комментарий платежа.");
					getState().setCommand(callCOMMENTSEARCHSETUP);
					break;
				}
				this.getState().getUser().setMessageId(0);
				doFindPaymentByWalletComment(argslist, sendMessage);
				defaultMenu(sendMessage);
				getState().setCommand("");
				//defaultMenu(sendMessage);
				logger.debug("this.doSendByExecute(sendMessage, update);");
				this.doSendByExecute(sendMessage, update);
				//processInitialView(sendMessage, update);
				return;

			default:
				sendMessage.setText("Не известная команда");
				break;
			}

			break;
		}

		String resAns = EmojiParser.parseToUnicode(sendMessage.getText());
		sendMessage.setText(resAns);
		if (this.getState().getMessageId() == 0) {
			this.doSendByExecute(sendMessage, update);
		} else {
			doEditMessage(sendMessage, update);
		}
		return;

	}

	public void doCallBack(CallbackQuery callBack, Update update) {
		logger.debug("[] doCallBack state:" + getState().getCommand());
		String callData = callBack.getData();
		logger.debug(callData);
		CallbackCommand calCom = CallbackCommand.getCommand(this.getControllerName(callData));
		logger.debug("COMMAND: " + calCom.toString());
		SendMessage sendMessage = new SendMessage(); // Create a message object object
		String text = callBack.getMessage().getText();
		long chatId = callBack.getMessage().getChatId();
		sendMessage.setChatId(chatId);
		sendMessage.setText(text);
		switch (calCom) {
		case QIWICALLBACK:
			logger.debug("IM IN QIWICALLBACK");
			logger.debug(callData);
			String[] args = callData.split(":");
			String action = args[1];
			logger.debug("ACTION: " + action);
			CallbackAction act = CallbackAction.getAction(action);
			List<String> argslist = new ArrayList<String>();
			int i = 2;
			while (i < args.length) {
				argslist.add(args[i]);
				i += 1;
			}
			logger.debug("ARG LIST: " + argslist.size());
			logger.debug(act);
			switch (act) {
			case HISTORY:
				logger.debug("OK IM IN HIST ACTION");

				this.getAccountHistory(argslist, sendMessage);

				defaultMenu(sendMessage);
				break;
			case REMOVE:
				logger.debug("OK IM IN REMOVE ACTION");
				this.doRemove(argslist, sendMessage);
				defaultMenu(sendMessage);
				break;
				
			case ACTIVATE:
				logger.debug("OK IM IN ACTIVATE ACTION");
				this.doActivate( argslist, sendMessage );
				defaultMenu(sendMessage);
				break;
				
			case ADD:
				logger.debug("OK IM IN ADD ACTION");
				if (argslist.size() < 2) {
					sendMessage.setText(
							":alien: Пришлите номер кошелька, для которого получен токен ( с международным префиксом, но без + ) пробел токен.\n"
									+ "Пример: 79112223344 YUu2qw048gtdsvlk3iu");
					getState().setCommand(callADD);
					break;
				}
				break;
			case WITHDRAWALLSETUP:
				logger.debug("OK IM IN WITHDRAWALLSETUP ACTION");
				if (argslist.size() < 2) {
					logger.debug("OK args<3");
					sendMessage.setText(":alien: Пришлите номер кошелька получателя пробел сумма.\n"
							+ "Пример: 79112223344 1500\n" + "максимальная сумма перевода"
							+ String.valueOf(transferLimit));
					getState().setCommand(callWITHDRAWALLSETUP);
					logger.debug("Set command: " + callWITHDRAWALLSETUP);
					defaultSmallMenu(sendMessage);
					doEditMessage(sendMessage, update);
					return;

				}
				getState().setCommand("");
				doTransfer(argslist, sendMessage);
				defaultMenu(sendMessage);
				break;

			case WITHDRAWALLHIST:
				logger.debug("> doCallBack > OK IM IN WITHDRAWALLHIST ACTION");
				if (argslist.size() < 2) {
					sendMessage.setText(":alien: Пришлите номер кошелька получателя\n" + "Пример: 79112223344\n");
					getState().setCommand(callWITHDRAWALLHIST);
					logger.debug("Set command: " + callWITHDRAWALLHIST);
					defaultSmallMenu(sendMessage);
					doEditMessage(sendMessage, update);
					return;
				}

				getState().setCommand("");
				doTransferHist(argslist, sendMessage);
				defaultMenu(sendMessage);
				return;

			case COMMENTSEARCH:
				logger.debug("OK IM IN COMMENTSEARCH ACTION");
				if (argslist.size() < 2) {
					sendMessage.setText(":alien: Пришлите номер кошелька пробел комментарий платежа.");
					getState().setCommand(callCOMMENTSEARCHSETUP);
					break;
				}
				break;

			case ACCOUNTS:
				processInitialView(sendMessage, update);

				break;
			
			case ACCOUNTSARCHIVE:
				//processInitialViewArchive(sendMessage, update);

				break;

			}
		}
		doEditMessage(sendMessage, update);
		return;

	}

	public void getAccountHistory(List<String> args, SendMessage sendMessage) {
		logger.debug("in: getAccountHistory ");
        List<Payment> paymentsList = getAccountService().getQIWI(args.get(0)).getPaymentsLast( 50 );
		if (isErrorInResp(paymentsList, sendMessage)) {
			return;
		}
		//logger.debug(paymentsList);

		//paymentsList = paymentsList.replaceAll("},", "},");
		//paymentsList =  paymentsList.replace("[", " ");
		//paymentsList = paymentsList.replace("]", " ");
		
		
		logger.debug(paymentsList);
		
		String answer = ":alien: История платежей:\n";

		for( Payment hs: paymentsList ) {

				answer += hs.getType()+"|"+hs.getSum().getAmount()+"| d="+hs.getDate().getDay()+":h="+hs.getDate().getHours()+":"
						+ hs.getDate().getMinutes()+" ";
				if(hs.getComment()!=null) {
					answer += "cmnt:"+	hs.getComment();
				}else {
					answer += "cmnt:null";
				}
				answer += "\n";

		}

		defaultSmallMenu(sendMessage);
		

		String resAns = EmojiParser.parseToUnicode(answer);
		logger.debug(resAns);
		sendMessage.setText(resAns);
		return;
	}
	

	

	public void defaultMenu(SendMessage sendMessage) {
		List<List<InlineKeyboardButton>> rowsInlineMenu = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> rowAccount = new ArrayList<InlineKeyboardButton>();

		String calldata = "@qiwi:{action}:args_?";

		rowsInlineMenu = makeDefaultMenu();

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		markupInline.setKeyboard(rowsInlineMenu);
		sendMessage.setReplyMarkup(markupInline);
		return;
	}
	
	
	public List<List<InlineKeyboardButton>> makeDefaultMenu() {
		List<List<InlineKeyboardButton>> rowsInlineMenu = new ArrayList<List<InlineKeyboardButton>>();
		

		String calldata = "@qiwi:{action}:args_?";

		String callres = "";
		
		List<InlineKeyboardButton> rowAccount = new ArrayList<InlineKeyboardButton>();
		if(qiwiService.getAccountList().size()==0) {
			callres = calldata.replace("{action}", CallbackAction.ADD.toString());
			rowAccount.add(new InlineKeyboardButton().setText("ДОБАВИТЬ").setCallbackData(callres));

			rowsInlineMenu.add(rowAccount);
		}
		List<InlineKeyboardButton> rowFind = new ArrayList<InlineKeyboardButton>();
		callres = calldata.replace("{action}", CallbackAction.COMMENTSEARCH.toString());
		rowFind.add(new InlineKeyboardButton().setText("НАЙТИ ПЛАТЕЖ").setCallbackData(callres));
		rowsInlineMenu.add( rowFind );

		rowAccount = new ArrayList<InlineKeyboardButton>();
		callres = calldata.replace("{action}", CallbackAction.WITHDRAWALLSETUP.toString());
		rowAccount.add(new InlineKeyboardButton().setText("ПЕРЕВОД").setCallbackData(callres));
		rowsInlineMenu.add(rowAccount);

		rowAccount = new ArrayList<InlineKeyboardButton>();
		callres = calldata.replace("{action}", CallbackAction.ACCOUNTS.toString());
		rowAccount.add(new InlineKeyboardButton().setText("🔂 МЕНЮ").setCallbackData(callres));
		rowsInlineMenu.add(rowAccount);
		//rowsInlineMenu.add(rowAccount);


		return rowsInlineMenu;
	}
	

	public void defaultSmallMenu(SendMessage sendMessage) {
		logger.debug("[] defaultSmallMenu");
		List<List<InlineKeyboardButton>> rowsInlineMenu = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> rowAccount = new ArrayList<InlineKeyboardButton>();

		rowsInlineMenu = makeDefaultSmallMenu();
		
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		markupInline.setKeyboard(rowsInlineMenu);
		sendMessage.setReplyMarkup(markupInline);
		return;
	}
	
	public List<List<InlineKeyboardButton>> makeDefaultSmallMenu() {
		logger.debug("[] defaultSmallMenu");
		List<List<InlineKeyboardButton>> rowsInlineMenu = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> rowAccount = new ArrayList<InlineKeyboardButton>();

		String calldata = "@qiwi:{action}:args_?";

		String callres = calldata.replace("{action}", CallbackAction.ACCOUNTS.toString());
		rowAccount.add(new InlineKeyboardButton().setText("🔂 МЕНЮ").setCallbackData(callres));
		rowsInlineMenu.add(rowAccount);
		
		return rowsInlineMenu;
	}

	public void doRemove(List<String> args, SendMessage sendMessage) {
		logger.debug("in: doRemove ");
		QIWISettings qiwiSettings = qiwiService.findQIWI( args.get(0));
		
		//NanoResponse resp = qiwiSrvArchive.archiveAccount( qi.getPhone(), qi.getToken() );
		
		if (isErrorInResp(qiwiSettings, sendMessage)) {
			return;
		}
		
		String answer = "👽 Результат операции:\n" ;
		

		qiwiService.removeAccount( qiwiSettings.getPhone() );
		
		answer += "\n1⃣ УДАЛЕН ИЗ СПИСКА АКТИВНЫХ";
		
		String resAns = EmojiParser.parseToUnicode(answer);
		//logger.debug(resAns);
		
		sendMessage.setText(resAns);
		return;
	}

	public void doAdd(List<String> args, SendMessage sendMessage) {
		logger.debug("in: doAdd ");
		QiwiResponse resp = getAccountService().addAccount(args.get(0), args.get(1));
		if (isErrorInResp(resp, sendMessage)) {
			return;
		}
		String answer = ":alien: Результат операции:\n" + resp.getStatus();
		
		String resAns = EmojiParser.parseToUnicode(answer);
		logger.debug(resAns);
		sendMessage.setText(resAns);
		return;
	}
	
	public void doActivate(List<String> args, SendMessage sendMessage) {
		logger.debug("in: doActivate ");

		/*String answer = ":alien: Результат операции:\n" + resp.getStatus();
		if( resp.getStatus().equals("SUCCESS") ) {
			qiwiSrvArchive.removeAccount( qiArch.getPhone() );
		}*/
		String answer = ":alien: Результат операции:\n";
		String resAns = EmojiParser.parseToUnicode(answer);
		logger.debug(resAns);
		sendMessage.setText(resAns);
		return;
	}

	/***
	 * 
	 * TRANSFER
	 * 
	 * @param args
	 * @param sendMessage
	 */

	public boolean doTransferCheck(List<String> args, SendMessage sendMessage) {
		logger.debug(" > doTransferCheck");
		String reciver = args.get(0);
		double amount = new Double(args.get(1)).doubleValue();
		String comment = null;
		if( args.size()==3 ) {
			comment = args.get(2);
		}
		
		List<QIWI> accs = this.getAccountService().getAccountList();
		if ( accs==null ) {
			return false;
		}
		
		// NOTIFY USER
				sendMessage.setText( ":alien: Проверяю  баланс и сумму на вывод...\n"
						+ "Это может занять некоторое время.\n" );
				this.doEditMessageSmall(sendMessage);
				
				// END: NOTIFY USER
		
		double sum = 0.0;
		
		for (QIWI acc : accs) {
			try {
				sum += acc.getBalanceRU();
				logger.debug("Try to sleep 5 sec");
				TimeUnit.SECONDS.sleep(5);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		String answer = "";
		if (sum < amount) {
			answer = ":alien: Не хватает средств.\n"
					+ "Баланс: " + this.doFormat(sum) + "\n";
			answer += "Вы хотите сделать перевод на сумму " + this.doFormat(amount);
			sendMessage.setText(answer);
			defaultMenu(sendMessage);
			return false;

		}

		answer = ":alien: Проверьте еще раз данные перед операцией\n" + "Ваш баланс: " + this.doFormat(sum)
				+ "\n";
		answer += "Вы хотите сделать перевод на сумму " + this.doFormat(amount) + "\n" + "На кошелек: " + reciver + "\n";
		if(comment!=null) {
			answer += "comment: " + comment+"\n";
		}
		answer += "__________________\n";
		answer += "Макстимальная сумма перевода: " + String.valueOf(transferLimit);
		answer += "\nКомиссия за перевод (0.02): " + String.valueOf(transferLimit * comission);
		sendMessage.setText(answer);

		return true;

	}

	void askBeforeDo(String calldata, SendMessage sendMessage) {
		System.out.print(" > askBeforeDo ");
		logger.debug(" : State.messId " + this.getState().getMessageId());
		logger.debug(calldata);
		List<List<InlineKeyboardButton>> rowsInlineMenu = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> rowAccount = new ArrayList<InlineKeyboardButton>();
		String callres = calldata + ":NO";
		rowAccount.add(new InlineKeyboardButton().setText("НЕТ").setCallbackData(callres));

		callres = calldata + ":YES";
		rowAccount.add(new InlineKeyboardButton().setText("ДА").setCallbackData(callres));

		rowsInlineMenu.add(rowAccount);
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		markupInline.setKeyboard(rowsInlineMenu);
		sendMessage.setReplyMarkup(markupInline);
		return;
	}



	public void doTransfer(List<String> args, SendMessage sendMessage) {

		String reciver = args.get(0);
		double amount = new Double(args.get(1)).doubleValue();
		String comment = null;
		if(args.size()==4) {
			comment = args.get(2);
		}
		String YESNO = args.get( args.size()-1 );
		String answer = ":alien: Результат операции:\n";
		if (YESNO.equals("NO")) {
			answer = ":alien: ФУХ!";
			sendMessage.setText(answer);
			return;
		}

		QIWI qiwi = qiwiService.getAccountList().get(0);
		Transaction transaction = qiwi.transferToWallet(reciver, amount, comment);
		
		answer += "________________\n"
				+ "ПЕРЕЧИСЛЕНО " + NumberString.printDouble(amount) + " code " + transaction.getState().getCode();
		
		sendMessage.setText(answer);

		return;

	}

	public void doTransferHist(List<String> args, SendMessage sendMessage) {

		String reciever = args.get(0);

		logger.debug("RECIEVER hist: " + reciever);

		String answer = "Получатель: " + reciever + "\n";
		answer = "Отправлено: {total}\n";
		double totalAmo = 0.0;
		/*List<TransferZero> transfers = this.transHist.findTransfer(reciever);

		double totalAmo = 0.0;
		for (TransferZero tr : transfers) {
			totalAmo += tr.getAmount();
			answer += "{date} | {amount} | comment:{comment}  \n";
			answer = answer.replace("{date}", tr.getDateTransfer().toString());
			answer = answer.replace("{amount}", String.valueOf(tr.getAmount()));
			answer = answer.replace("{comment}", String.valueOf(tr));
		}*/
		answer = answer.replace("{total}", String.valueOf(totalAmo));
		logger.debug(answer);
		sendMessage.setText(answer);
		return;

	}

	protected boolean isErrorInResp(Object resp, SendMessage sendMessage) {
		if (resp == null) {
			String answer = ":alien: Сервис не отвечает!\n";
			sendMessage.setText(answer);
			return true;
		}
		return false;
	}

	protected void processInitialView(SendMessage sendMessage, Update update) {
		logger.debug("IN METHOD processInitialView");
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		
		qiwiService.loadQIWI();
		
		List<QIWI> accountList = qiwiService.getAccountList();
		
		// NOTIFY USER
		if(accountList.size()>0) {
			int sec = 2*accountList.size();
		
			sendMessage.setText(":alien: Запрашиваю у киви актуальные данные ...\n"
						+ "Это может занять 🚬 "+ sec+" sec");
			if(getState().getUser().getMessageId()>0) {
				this.doEditMessageSmall(sendMessage);
			}else {
				this.doSendByExecute(sendMessage, update);
			}
				
		}
		// END: NOTIFY USER

		double sum = 0.0;
		List<List<InlineKeyboardButton>> rowsInlineMenu = new ArrayList<List<InlineKeyboardButton>>();

		for (QIWI acc : accountList) {
			double amount = acc.getBalanceRU();
			sum += amount;
			List<InlineKeyboardButton> rowAccount = new ArrayList<InlineKeyboardButton>();
			String calldata = "@qiwi:{action}:" + acc.getPhone() + "";
			rowAccount.add(
					new InlineKeyboardButton().setText(acc.getPhone() + " 💳  " + this.doFormat( amount ))
							.setCallbackData(calldata.replace("{action}", CallbackAction.HISTORY.toString())));
			rowsInlineMenu.add(rowAccount);
			List<InlineKeyboardButton> rowActions = new ArrayList<InlineKeyboardButton>();

			String callres = calldata.replace("{action}", CallbackAction.REMOVE.toString());
			rowActions.add(new InlineKeyboardButton().setText("УДАЛИТЬ").setCallbackData(callres));
			callres = calldata.replace("{action}", CallbackAction.HISTORY.toString());
			rowActions.add(new InlineKeyboardButton().setText("ИСТОРИЯ IN").setCallbackData(callres));
			rowsInlineMenu.add(rowActions);
			
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (Exception ex) {
				logger.debug("Fail to sleep 2 sec");
				ex.printStackTrace();
			}
			
		}

		
		List<List<InlineKeyboardButton>> rowsInlineMenuDef = this.makeDefaultMenu();
		
		rowsInlineMenu.addAll( rowsInlineMenuDef );
		
		markupInline.setKeyboard(rowsInlineMenu);
		
		sendMessage.setReplyMarkup(markupInline);

		String answer = "Ваш баланс {sum} \n:alien: ";
		String res = answer.replace("{sum}", this.doFormat(sum));
		String res2 = EmojiParser.parseToUnicode(res);
		sendMessage.setText(res2);
		return;
	}


	

	protected String getControllerName(String callData) {
		String name = callData.trim();
		String[] args = name.split(":");
		return args[0];
	}

	public void doFindPaymentByWalletComment(List<String> args, SendMessage sendMessage) {
		logger.debug("in: doFindPaymentByWalletComment ");
		String resp = getAccountService().findPayment(args.get(0), args.get(1));
		if (isErrorInResp(resp, sendMessage)) {
			return;
		}
		String answer = ":alien: Результат операции:\n" + resp;

		String resAns = EmojiParser.parseToUnicode(answer);
		logger.debug(resAns);
		sendMessage.setText(resAns);
		return;
	}

}
