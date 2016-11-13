package com.webinfo.security.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectToString {
	public final static String convertToString(Object obj) throws Exception {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		byte[] bytes;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			bytes = bos.toByteArray();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (bytes == null || bytes.length == 0)
			return null;

		bytes = GZip.compress(bytes);
		if (bytes == null || bytes.length == 0)
			return null;

		bytes = AES.encrypt(bytes);
		if (bytes == null || bytes.length == 0)
			return null;

		return BytesToString.convertToString(bytes);
	}

	public final static Object convertToObject(String string) throws Exception {
		if (CommonUtil.isEmpty(string))
			return null;
		byte[] bytes = BytesToString.convertToBytes(string);
		if (bytes == null || bytes.length == 0)
			return null;

		bytes = AES.decrypt(bytes);
		if (bytes == null || bytes.length == 0)
			return null;

		bytes = GZip.uncompress(bytes);
		if (bytes == null || bytes.length == 0)
			return null;

		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		Object obj;

		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			obj = ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
			}

			if (bis != null) {
				bis.close();
			}
		}

		return obj;
	}

}
