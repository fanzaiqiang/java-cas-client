package com.webinfo.security.util;

public class CommonUtil {
	public final static boolean isEmpty(String s) {
		if (s == null)
			return true;

		if (s.trim().equals(""))
			return true;

		return false;

	}

}
