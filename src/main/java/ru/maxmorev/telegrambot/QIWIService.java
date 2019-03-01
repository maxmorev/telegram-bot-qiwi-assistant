package ru.maxmorev.telegrambot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ru.maxmorev.payment.qiwi.QIWI;
import ru.maxmorev.payment.qiwi.response.Payment;
import ru.maxmorev.payment.qiwi.response.QiwiResponse;
import ru.maxmorev.telegrambot.core.QiwiPollingBot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class QIWIService {

	final static Logger logger = Logger.getLogger(QIWIService.class);

	QIWISettingsList qiwiSettingsList = new QIWISettingsList();

	private List<QIWI> qiwiAccounts = new ArrayList<>();

	String file = "qiccounts.json";

	public QIWIService(String file) {
		super();
		this.file = file;
		this.loadQIWI();
	}



	public void loadQIWI() {

		String jsonStr;
		try {
			ObjectMapper mapper = new ObjectMapper();
			qiwiSettingsList = mapper.readValue(new File(file), QIWISettingsList.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}catch (Exception ex){
			ex.printStackTrace();
		}

	}

	public void saveQIWI() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File(file), this.qiwiSettingsList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public QIWISettings findQIWI(String phone) {
		QIWISettings res = null;

		Optional<QIWISettings> findFirst = qiwiSettingsList.getData().stream().filter(qiwi -> qiwi.getPhone().equals(phone)).findFirst();
		if(findFirst.isPresent()) {
			res = findFirst.get();
		}
		return res;
	}

	public QiwiResponse addAccount(String phone, String token) {
		QiwiResponse respNano = new QiwiResponse();
		respNano.setStatus("Ошибка");
		if (findQIWI(phone) != null) {
			respNano.setStatus("УЖЕ ДОБАВЛЕН");
			return respNano;
		}

		
		QIWI qiwi = null;
		try{
			qiwi = new QIWI(phone, token);
		}catch (Exception ex){
			respNano.setMessage(ex.getMessage());
			respNano.setStatus("FAIL");
		}

		if (qiwi!=null) {
			QIWISettings qiwiSettings = new QIWISettings(phone, token);
			qiwiSettingsList.getData().add(qiwiSettings);
			saveQIWI();
			respNano.setStatus("SUCCESS");

		}
		return respNano;

	}


	public QiwiResponse removeAccount(String phone) {

		QiwiResponse respNano = new QiwiResponse();
		respNano.setStatus("FAIL");

		int idx = 0;
		for (QIWISettings qi : qiwiSettingsList.getData()) {
			if (qi.getPhone().equals(phone)) {
				try {
					qiwiSettingsList.getData().remove(idx);
					respNano.setStatus("SUCCESS");
					saveQIWI();
					return respNano;
				} catch (Exception ex) {
					respNano.setStatus("FAIL");
					respNano.setMessage(ex.getMessage());
					return respNano;
				}

			}
			idx++;
		}

		return respNano;

	}

	public String findPayment(String phone, String comment) {
		System.out.println("findPayment(" + phone + "," + comment + ")");
		String resp = "0";
		double sum = 0.0;
		//list.stream().filter(p -> p.getName().equals(name)).findAny();

		Optional<QIWISettings> findFirst = qiwiSettingsList.getData().stream().filter(qiwi -> qiwi.getPhone().equals(phone)).findFirst();
		if(findFirst.isPresent()){
			QIWI qiwi = new QIWI(findFirst.get().getPhone(), findFirst.get().getToken());
			List<Payment> paymentList = qiwi.getPaymentsLast(50);
			//
			if(paymentList!=null && paymentList.size()>0) {
				System.out.println(paymentList.size());
				Optional<Payment> findFirstPayment =paymentList.stream().filter(payment -> payment.getAccount().contains(comment)).findFirst();
				if( findFirstPayment.isPresent() ){
					sum = findFirstPayment.get().getSum().getAmount();
				}
			}
		}
		resp = String.valueOf(sum);
		return resp;

	}

	public List<QIWI> getAccountList() {
		List<QIWI> accountList = new ArrayList<>();
		for(QIWISettings qiwiSettings: qiwiSettingsList.getData()){
			QIWI qiwi = null;
			try{
				qiwi = new QIWI(qiwiSettings.getPhone(), qiwiSettings.getToken());
				accountList.add(qiwi);
			}catch (Exception ex){
				//qiwiSettingsList.getData().remove(qiwiSettings);
			}
			if(qiwi!=null){
				qiwiAccounts.add(qiwi);
			}
		}

		return accountList;

	}

	public QIWI getQIWI(String phone) {

		QIWI resp = null;

		QIWISettings qiwiSettings = findQIWI(phone);
		try{
			resp = new QIWI(qiwiSettings.getPhone(), qiwiSettings.getToken());
		}catch (Exception ex){

		}

		return resp;

	}


	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
