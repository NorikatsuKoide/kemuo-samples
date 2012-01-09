package com.yohpapa.research.cookiesample;

import android.app.Application;

public class CookieSampleApp extends Application {
	
	public void storeCookie(String cookies) {
		CookieSamplePreferences.storeCookie(this, cookies);
	}
	
	public String restoreCookie() {
		return CookieSamplePreferences.restoreCookie(this);
	}
}
