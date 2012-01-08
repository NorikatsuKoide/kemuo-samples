package com.yohpapa.research.cookiesample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yohpapa.tools.ui.ActionBarActivity;

public class CookieSampleWebView extends ActionBarActivity {
	private static final String TAG = CookieSampleWebView.class.getSimpleName();

	private static final String LOGIN_URL = "https://accounts.google.com/accounts/ServiceLogin?hl=ja&nui=1&service=hist";
	private static final String LOGIN_FINISH_URL = "https://www.google.com/settings/";
	
	private WebView _webView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
		
		_webView = (WebView)findViewById(R.id.login_webview);
		_webView.setWebViewClient(_webViewClient);
		_webView.getSettings().setJavaScriptEnabled(true);
		_webView.loadUrl(LOGIN_URL);
	}
	
	private final WebViewClient _webViewClient = new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {
			if(url.indexOf(LOGIN_FINISH_URL) != 0) {
				if(!url.equals(LOGIN_URL)) {
					_webView.setVisibility(View.INVISIBLE);
				}
				return;
			}
			
			String cookies = CookieManager.getInstance().getCookie(LOGIN_URL);
			if(cookies == null) {
				Log.e(TAG, "Error: cookies is null.");
				return;
			}
			
			Log.v(TAG, "cookies: " + cookies);
			
			// TODO:
			// Cookie情報をContentProviderに保存しておく
			// その時、暗号化やセキュリティ設定を完璧にしておくこと
			
			((CookieSampleApp)getApplication()).storeCookie(cookies);
			
			// Cookieを取得したら元の画面に戻る
			Intent intent = new Intent();
			setResult(0, intent);
			finish();
		}
	};
}
