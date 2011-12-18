package com.yohpapa.research.searchsample;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class IntentBuilder {
	
	public static Intent build(String path, String action) {
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		String mime = getMimeType(file.getName());
		if(mime == null)
			return null;

		Intent intent = new Intent();
		intent.setAction(action);
		intent.setDataAndType(uri, mime);
		
		return intent;
	}
	
	private static String getMimeType(String fileName) {
		int position = fileName.lastIndexOf(".");
		if(position == -1)
			return fileName;
		
		String extension = fileName.substring(position + 1);
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}
}
