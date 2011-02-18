package com.appspot.perfectgames.util;

public class StringUtility {
	public static String trim(String str) {
		if (str == null) {
			str = "";
		}
		str = str.replace("\t", "");
		str = str.replace("\r", "");
		str = str.replace("\n", "");
		return str;
	}
}
