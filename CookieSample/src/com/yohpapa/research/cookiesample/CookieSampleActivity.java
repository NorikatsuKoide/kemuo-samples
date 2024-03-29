package com.yohpapa.research.cookiesample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yohpapa.tools.ui.ActionBarActivity;

public class CookieSampleActivity extends ActionBarActivity {

	@SuppressWarnings("unused")
	private static final String TAG = CookieSampleActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String cookie = ((CookieSampleApp)getApplication()).restoreCookie();
		if(cookie == null)
			return;
		
		updateCookieList(cookie);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != 0) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		
		String cookie = ((CookieSampleApp)getApplication()).restoreCookie();
		if(cookie == null) {
			Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
			return;
		}
		
		updateCookieList(cookie);
	}
	
	private void updateCookieList(String cookie) {
		String[] cookies = cookie.split(";");
		
		SimpleAdapter adapter = new SimpleAdapter(
											this,
											getListData(cookies),
											R.layout.list_item,
											new String[] {"LABEL", "CONTENT"},
											new int[] {R.id.cookie_label, R.id.cookie_content});
		
		ListView list = (ListView)findViewById(R.id.cookie_list);
		list.setAdapter(adapter);
	}
	
	private List<Map<String, String>> getListData(String[] cookies) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		for(String cookie : cookies) {
			String[] buf = cookie.trim().split("=");
			list.add(getMapData(buf[0], buf[1]));
		}
		
		return list;
	}
	
	private Map<String, String> getMapData(String data1, String data2) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("LABEL", data1);
		map.put("CONTENT", data2);
		return map;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;
		Intent intent;
		int itemId = item.getItemId();
		switch(itemId) {
		case R.id.menu_login:
			intent = new Intent(this, CookieSampleWebView.class);
			startActivityForResult(intent, 0);
			break;
			
		case R.id.menu_rss_recent:
			onMenuRssSelected(CookieSampleRssView.RECENT);
			break;
			
		case R.id.menu_rss_yesterday:
			onMenuRssSelected(CookieSampleRssView.YESTERDAY);
			break;
			
		case R.id.menu_rss_last_week:
			onMenuRssSelected(CookieSampleRssView.LAST_WEEK);
			break;
			
		case R.id.menu_rss_last_month:
			onMenuRssSelected(CookieSampleRssView.LAST_MONTH);
			break;
			
		case R.id.menu_rss_more_past:
			onMenuRssSelected(CookieSampleRssView.MORE_PAST);
			break;
			
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		
		return result;
	}
	
	private void onMenuRssSelected(int param) {
		final String cookie = ((CookieSampleApp)getApplication()).restoreCookie();
		if(cookie == null) {
			Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(this, CookieSampleRssView.class);
		intent.putExtra(CookieSampleRssView.START_PARAMETER, param);
		startActivity(intent);

	}
}