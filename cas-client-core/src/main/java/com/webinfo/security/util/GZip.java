package com.webinfo.security.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {
	public static byte[] compress(byte[] data) throws Exception {
		if (data == null || data.length == 0)
			return null;

		ByteArrayOutputStream bos = null;
		GZIPOutputStream zos = null;
		byte[] bytes = null;
		try {
			bos = new ByteArrayOutputStream();
			zos = new GZIPOutputStream(bos);
			zos.write(data);
			zos.finish();
			bytes = bos.toByteArray();
		} finally {
			try {
				if (zos != null) {
					zos.close();
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
		return bytes;
	}

	public static byte[] uncompress(byte[] data) throws Exception {
		if (data == null || data.length == 0) {
			return null;
		}
		ByteArrayInputStream bis = null;
		GZIPInputStream zis = null;
		ByteArrayOutputStream bos = null;
		byte[] bytes = null;
		byte[] buf = new byte[1024];
		int num = -1;
		try {
			bis = new ByteArrayInputStream(data);
			zis = new GZIPInputStream(bis);
			bos = new ByteArrayOutputStream();
			while ((num = zis.read(buf, 0, buf.length)) != -1) {
				bos.write(buf, 0, num);
			}
			bytes = bos.toByteArray();
		} finally {
			try {
				if (zis != null) {
					zis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (bis != null) {
					bis.close();
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
		return bytes;
	}
}
