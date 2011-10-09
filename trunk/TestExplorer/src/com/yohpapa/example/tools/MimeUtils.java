package com.yohpapa.example.tools;

import android.webkit.MimeTypeMap;

public class MimeUtils {

	/**
	 * MIME typeを取得する
	 * @param fileName
	 * @return
	 */
	public static String getMimeType(String fileName) {
		String extension = getExtension(fileName).toLowerCase();
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}

	/**
	 * ファイルの拡張子を取得する
	 * @param fileName
	 * @return
	 */
	private static String getExtension(String fileName) {
		if(fileName == null)
			return null;

		int position = fileName.lastIndexOf(".");
		if(position == -1)
			return fileName;

		return fileName.substring(position + 1);
	}
}
