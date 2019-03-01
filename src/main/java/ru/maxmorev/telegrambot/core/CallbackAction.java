package ru.maxmorev.telegrambot.core;

public enum CallbackAction {
	
	REMOVE, ADD, HISTORY,
	WITHDRAWALLSETUP, WITHDRAWALL, ACCOUNTS, ACCOUNTSARCHIVE, ACTIVATE, COMMENTSEARCH, DEFACTION, WITHDRAWALLHIST;
	
	public static CallbackAction getAction(String name) {
		if( name.equals("HISTORY")) {
			return HISTORY;
		}
		if( name.equals("REMOVE")) {
			return REMOVE;
		}
		if( name.equals("ADD")) {
			return ADD;
		}
		if( name.equals("WITHDRAWALL")) {
			return WITHDRAWALL;
		}
		if( name.equals("ACCOUNTS")) {
			return ACCOUNTS;
		}
		if( name.equals("ACCOUNTSARCHIVE")) {
			return ACCOUNTSARCHIVE;
		}

		if( name.equals("ACTIVATE")) {
			return ACTIVATE;
		}

		if( name.equals("WITHDRAWALLSETUP")) {
			return WITHDRAWALLSETUP;
		}
		if( name.equals("COMMENTSEARCH")) {
			return COMMENTSEARCH;
		}

		if( name.equals("WITHDRAWALLHIST") ) {
			return WITHDRAWALLHIST;

		}

		return DEFACTION;
	}

}
