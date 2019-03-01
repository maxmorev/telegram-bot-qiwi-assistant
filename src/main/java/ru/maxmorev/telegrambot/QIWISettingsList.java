package ru.maxmorev.telegrambot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QIWISettingsList {

    List<QIWISettings> data = new ArrayList<QIWISettings>();

    public List<QIWISettings> getData() {
        return data;
    }

    public void setData(List<QIWISettings> data) {
        this.data = data;
    }


}
