package com.yohpapa.research.cookiesample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CookieSamplePreferences {
	private static SharedPreferences.Editor getEditor(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if(pref == null)
			return null;
		
		return pref.edit();
	}
	
	public static void storeCookie(Context context, String cookie) {
		SharedPreferences.Editor editor = getEditor(context);
		if(editor == null)
			return;
		
		editor.putString(context.getString(R.string.pref_cookie), cookie);
		editor.commit();
	}
	
	public static String restoreCookie(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if(pref == null)
			return null;
		
		return pref.getString(context.getString(R.string.pref_cookie), null);
	}
}
