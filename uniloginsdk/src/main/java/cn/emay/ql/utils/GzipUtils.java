package cn.emay.ql.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP 压缩工具
 * 
 * @author Frank
 *
 */
public class GzipUtils {

	public static byte[] compress(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzipInputStream;
		try {
			gzipInputStream = new GZIPOutputStream(out);
			gzipInputStream.write(str.getBytes("UTF-8"));
			gzipInputStream.close();
		} catch (IOException e) {
			System.out.println("gzip compress error");
		}
		return out.toByteArray();
	}

	/**
	 * 字节数组解压
	 */
	public static byte[] decompress(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			GZIPInputStream gzipInputStream = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gzipInputStream.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			System.out.println("gzip uncompress error.");
		}

		return out.toByteArray();
	}

}
