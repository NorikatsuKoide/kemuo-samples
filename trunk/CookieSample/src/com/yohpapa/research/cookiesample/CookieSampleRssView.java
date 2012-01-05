package com.yohpapa.research.cookiesample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.yohpapa.tools.ui.ActionBarActivity;

public class CookieSampleRssView extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_view);
		
		String cookie = ((CookieSampleApp)getApplication()).restoreCookie();
		RssTask task = new RssTask();
		task.execute(cookie);
	}
	
	private class RssTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String cookie = params[0];
			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.useragent", "Mozilla/5.0");
			String[] cookies = cookie.split(";");
			for(String value : cookies) {
				value = value.trim();
				String[] cookieSet = value.split("=");
				BasicClientCookie bCookie = new BasicClientCookie(cookieSet[0], cookieSet[1]);
				bCookie.setDomain("google.com");
				bCookie.setPath("/");
				CookieStore store = client.getCookieStore();
				store.addCookie(bCookie);
			}
			
			BufferedReader reader = null;
			HttpGet request = new HttpGet("https://www.google.com/history/?output=rss&hl=ja&lr=lang_ja");
			try {
				HttpResponse response = client.execute(request);
				
				// ここでレスポンスのRSSを解析する
				if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
					return null;
				
				HttpEntity entity = response.getEntity();
				reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				
				StringBuilder builder = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}
				
				return builder.toString();
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onPostExecute(String result) {
			TextView text = (TextView)CookieSampleRssView.this.findViewById(R.id.rss_box);
			if(text == null)
				return;
			
			text.setText(result);
		}
	}
}
