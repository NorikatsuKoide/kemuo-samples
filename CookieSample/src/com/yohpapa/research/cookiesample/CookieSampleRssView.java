package com.yohpapa.research.cookiesample;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.yohpapa.tools.rss.RssItem;
import com.yohpapa.tools.rss.WebHistoryParser;
import com.yohpapa.tools.ui.ActionBarActivity;

public class CookieSampleRssView extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_view);
		
		// Cookie文字列を使ってWeb履歴RSSを取得する
		String cookie = ((CookieSampleApp)getApplication()).restoreCookie();
		WebHistoryTask task = new WebHistoryTask();
		task.execute(cookie);
	}
	
	private class WebHistoryTask extends AsyncTask<String, Void, List<RssItem>> {
		@Override
		protected List<RssItem> doInBackground(String... params) {
			// Cookieを使ってGoogle Web履歴のRSSを解析する
			String cookie = params[0];
			WebHistoryParser parser = new WebHistoryParser("https://www.google.com/history/?output=rss&hl=ja&lr=lang_ja");
			parser.setCookie(cookie);
			return parser.parse();
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
