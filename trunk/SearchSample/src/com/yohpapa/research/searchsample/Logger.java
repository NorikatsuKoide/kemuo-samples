package com.yohpapa.research.searchsample;

import android.util.Log;

public class Logger {
	private static boolean D = true;
	
	static public void d(String tag, String message) {
		if(!D)
			return;
		
		Log.d(tag, ">>>>>>>>>> " + message + " <<<<<<<<<<");
	}
}
