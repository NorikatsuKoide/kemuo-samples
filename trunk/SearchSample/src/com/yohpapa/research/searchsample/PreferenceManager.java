/**
Copyright (c) 2011, KENSUKE NAKAI
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list
  of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.
* Neither the name of the nakaikensuke.com nor the names of its contributors may
  be used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
OF SUCH DAMAGE.
*/
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
	
	public static boolean setAppFirstInvoked(Context context, boolean isStarted) {
		SharedPreferences pref = context.getSharedPreferences(
												context.getString(R.string.prefs_name),
												Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(
					context.getString(R.string.prefs_key_app_first_invoke),
					isStarted);
		
		return editor.commit();
	}
	
	public static boolean getAppFirstInvoked(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
												context.getString(R.string.prefs_name),
												Context.MODE_PRIVATE);
		return pref.getBoolean(
						context.getString(R.string.prefs_key_app_first_invoke),
						false);
	}
	
	public static boolean setShortNameAvailable(Context context, boolean isStarted) {
		SharedPreferences pref = context.getSharedPreferences(
												context.getString(R.string.prefs_name),
												Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(
					context.getString(R.string.prefs_key_shortname_available),
					isStarted);
		
		return editor.commit();
	}
	
	public static boolean getShortNameAvailable(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
												context.getString(R.string.prefs_name),
												Context.MODE_PRIVATE);
		return pref.getBoolean(
						context.getString(R.string.prefs_key_shortname_available),
						false);
	}
}