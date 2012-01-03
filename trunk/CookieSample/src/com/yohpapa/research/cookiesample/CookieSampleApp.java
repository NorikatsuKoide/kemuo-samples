package com.yohpapa.research.cookiesample;

import android.app.Application;

public class CookieSampleApp extends Application {
	private String _cookies = null;
	
	public void storeCookie(String cookies) {
		_cookies = cookies;
	}
	
	public String restoreCookie() {
		return _cookies;
	}
}
