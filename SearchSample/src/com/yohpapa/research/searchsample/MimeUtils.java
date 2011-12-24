package com.yohpapa.research.searchsample;

import android.webkit.MimeTypeMap;

public class MimeUtils {
	public static String getMimeType(String fileName) {
		
		// 最後のピリオドの位置を特定する
		int position = fileName.lastIndexOf(".");
		if(position == -1)
			return fileName;
		
		// 最後のピリオドの一つ後ろからが拡張子の始まりとなる
		String extension = fileName.substring(position + 1);
		
		// 拡張子を元にMIME種別を割り出す
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}
}
