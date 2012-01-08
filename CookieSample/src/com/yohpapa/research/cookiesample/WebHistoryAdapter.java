package com.yohpapa.research.cookiesample;

import java.util.List;

import com.yohpapa.tools.rss.RssItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WebHistoryAdapter extends ArrayAdapter<RssItem> {
	private static final String CATEGORY_WEB_QUERY = "web query";
	
	public WebHistoryAdapter(Context context, List<RssItem> items) {
		super(context, R.layout.history_list_item, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// リスト項目のViewが未作成の場合は作成する
		View view = convertView;
		if(view == null) {
			Context context = getContext();
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.history_list_item, null);
		}
		
		// リスト位置が持っているデータよりも多い場合は何もしない (出来ない)
		if(position >= getCount())
			return view;
		
		// リスト位置に相当するエントリ情報を抽出する
		final RssItem item = getItem(position);
		if(item == null)
			return view;
		
		// 項目種別アイコンを設定する
		ImageView icon = (ImageView)view.findViewById(R.id.history_type_icon);
		if(icon == null)
			return view;
		if(CATEGORY_WEB_QUERY.equals(item.getCategory())) {
			icon.setImageResource(R.drawable.ic_query);
		} else {
			icon.setImageResource(R.drawable.ic_result);
		}
		
		// 履歴タイトルを設定する
		TextView title = (TextView)view.findViewById(R.id.history_title);
		if(title == null)
			return view;
		title.setText(item.getTitle());
		
		// 履歴日時を設定する
		TextView date = (TextView)view.findViewById(R.id.history_date);
		if(date == null)
			return view;
		date.setText(item.getPubDate().toLocaleString());

		return view;
	}
}
