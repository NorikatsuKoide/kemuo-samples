package com.yohpapa.research.searchsample;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

public class IntentBuilder {
	
	public static Intent build(String path, String action) {
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		String mime = MimeUtils.getMimeType(file.getName());
		if(mime == null)
			return null;

		Intent intent = new Intent();
		intent.setAction(action);
		intent.setDataAndType(uri, mime);
		
		return intent;
	}
}
