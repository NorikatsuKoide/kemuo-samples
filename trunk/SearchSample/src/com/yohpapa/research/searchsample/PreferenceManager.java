package com.yohpapa.research.searchsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {

	public static boolean setNameType(Context context, boolean isShort) {
		SharedPreferences pref = context.getSharedPreferences(
												context.getString(R.string.prefs_name),
												Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(
					context.getString(R.string.prefs_key_name_type),
					isShort);

		return editor.commit();
	}
	
	public static boolean getNameType(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
												context.getString(R.string.prefs_name),
												Context.MODE_PRIVATE);
		return pref.getBoolean(
						context.getString(R.string.prefs_key_name_type),
						true);
	}
}
