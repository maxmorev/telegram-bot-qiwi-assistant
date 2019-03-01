package ru.maxmorev.utill;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberString {
	
	public static String PRINTFORMAT="0.###";
	public static String PRINTFORMATBTC="0.#######";
	
	public static String printDouble(Double amount) {
		NumberFormat formatter = new DecimalFormat();
		formatter = new DecimalFormat(PRINTFORMAT);
		String cute = formatter.format(amount);
		return cute;
	}
	
	public static String printBTC(Double amount) {
		NumberFormat formatter = new DecimalFormat();
		formatter = new DecimalFormat( PRINTFORMATBTC );
		String cute = formatter.format(amount);
		return cute;
	}

}
