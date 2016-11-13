package com.webinfo.security.util;

public class BytesToString {
	public static String convertToString(byte[] source) {
		if (source == null || source.length == 0)
			return null;

		StringBuilder sb = new StringBuilder("");
		int v;
		String hv;
		for (int i = 0; i < source.length; i++) {
			v = source[i] & 0xFF;
			hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
		}
		return sb.toString();
	}

	public static byte[] convertToBytes(String source) {
		if (source == null || source.equals("")) {
			return null;
		}
		source = source.toUpperCase();
		int length = source.length() / 2;
		char[] hexChars = source.toCharArray();
		byte[] d = new byte[length];
		int pos;
		for (int i = 0; i < length; i++) {
			pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
