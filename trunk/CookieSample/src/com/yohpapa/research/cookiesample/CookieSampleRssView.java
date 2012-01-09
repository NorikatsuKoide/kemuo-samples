package com.yohpapa.research.cookiesample;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.yohpapa.tools.rss.RssItem;
import com.yohpapa.tools.rss.WebHistoryParser;
import com.yohpapa.tools.ui.ActionBarActivity;

public class CookieSampleRssView extends ActionBarActivity {
	public static final String START_PARAMETER = "START_PARAMETER";
	public static final int RECENT = 0;
	public static final int YESTERDAY = 1;
	public static final int LAST_WEEK = 2;
	public static final int LAST_MONTH = 3;
	public static final int MORE_PAST = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_view);
		
		Intent intent = getIntent();
		if(intent == null) {
			finish();
			return;
		}
		
		int param = intent.getIntExtra(START_PARAMETER, -1);
		if(param == -1) {
			finish();
			return;
		}
		
		// Cookie文字列を使ってWeb履歴RSSを取得する
		String cookie = ((CookieSampleApp)getApplication()).restoreCookie();
		WebHistoryTask task = new WebHistoryTask(param);
		task.execute(cookie);
	}
	
	private class WebHistoryTask extends AsyncTask<String, Void, List<RssItem>> {
		private final long _maxTime;
		private final long _minTime;
		
		public WebHistoryTask(int param) {
			switch(param) {
			case RECENT:
				_maxTime = System.currentTimeMillis() * 1000L;
				_minTime = getEpocTimeDayOffset(0, true);
				break;
				
			case YESTERDAY:
				_maxTime = getEpocTimeDayOffset(-1, false);
				_minTime = getEpocTimeDayOffset(-1, true);
				break;
				
			case LAST_WEEK:
				_maxTime = getEpocTimeDayOffset(-2, false);
				_minTime = getEpocTimeDayOffset(-7, true);
				break;
				
			case LAST_MONTH:
				_maxTime = getEpocTimeDayOffset(-8, false);
				_minTime = getEpocTimeMonthOffset(-1, true);
				break;
				
			case MORE_PAST:
				_maxTime = getEpocTimeMonthOffset(-1, true) - 1;
				_minTime = 0;
				break;
				
			default:
				throw new RuntimeException("Intent parameter invalid");
			}
		}
		
		private long getEpocTimeDayOffset(int dayOffset, boolean isStart) {
			Calendar date = Calendar.getInstance();
			date.add(Calendar.DATE, dayOffset);
			return getOffsetTime(date, isStart);
		}
		
		private long getOffsetTime(Calendar date, boolean isStart) {
			int year = date.get(Calendar.YEAR);
			int month = date.get(Calendar.MONTH);
			int day = date.get(Calendar.DATE);
			int hour = 0;
			int minute = 0;
			int second = 0;
			if(!isStart) {
				hour = 23;
				minute = 59;
				second = 59;
			}
			return new GregorianCalendar(year, month, day, hour, minute, second).getTimeInMillis() * 1000L;
		}
		
		private long getEpocTimeMonthOffset(int monthOffset, boolean isStart) {
			Calendar date = Calendar.getInstance();;
			date.add(Calendar.MONDAY, monthOffset);
			return getOffsetTime(date, isStart);
		}
		
		private static final String URL = "https://www.google.com/history/lookup?output=rss&hl=ja&lr=lang_ja&max=%d";

		@Override
		protected List<RssItem> doInBackground(String... params) {
			// Cookieを使ってGoogle Web履歴のRSSを解析する
			String cookie = params[0];
			long maxTime = _maxTime;
			boolean isLoop = true;
			List<RssItem> result = new ArrayList<RssItem>();
			while(isLoop) {
				String url = String.format(URL, maxTime);
				WebHistoryParser parser = new WebHistoryParser(url);
				parser.setCookie(cookie);
				
				List<RssItem> buffer = parser.parse();
				if(buffer == null || buffer.size() <= 0)
					return null;
				
				Date limit = new Date(_minTime / 1000);
				for(int i = 0; i < buffer.size(); i ++) {
					RssItem item = buffer.get(i);
					if(limit.compareTo(item.getPubDate()) > 0) {
						if(i > 0) {
							result.addAll(buffer.subList(0, i - 1));
						}
						return result;
					}
				}
				
				result.addAll(buffer);

				RssItem lastItem = buffer.get(buffer.size() - 1);
				maxTime = lastItem.getPubDate().getTime() * 1000 - 1;
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(List<RssItem> result) {
			if(result == null)
				return;
			
			// 取得したRSSリストをリスト表示する
			ListView list = (ListView)findViewById(R.id.web_history_list);
			WebHistoryAdapter adapter = new WebHistoryAdapter(CookieSampleRssView.this, result);
			list.setAdapter(adapter);
		}
	}
}
