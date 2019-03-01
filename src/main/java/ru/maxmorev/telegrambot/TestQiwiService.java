package ru.maxmorev.telegrambot;

import ru.maxmorev.payment.qiwi.QIWI;

import java.util.List;

public class TestQiwiService {

    public static void main(String... args){

        QIWIService qiwiService = new QIWIService("qiwiAccounts.json");
        qiwiService.loadQIWI();
        List<QIWI> qiwiList = qiwiService.getAccountList();
        for(QIWI qiwi: qiwiList){
            System.out.println(qiwi.getPhone()+" " + qiwi.getToken());
        }
        //qiwiService.addAccount("79263926369", "96cb82d84820171c774250e1ed082a99");
        //qiwiService.saveQIWI();



    }

}
