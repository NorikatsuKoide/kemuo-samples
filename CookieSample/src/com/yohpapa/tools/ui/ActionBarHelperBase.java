package com.yohpapa.tools.ui;

/**
Copyright (c) 2011-2012, KENSUKE NAKAI
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yohpapa.research.cookiesample.R;

public class ActionBarHelperBase extends ActionBarHelper {
	private static final String MENU_RES_NAMESPACE = "http://schemas.android.com/apk/res/android";
	private static final String MENU_ATTR_ID = "id";
	private static final String MENU_ATTR_SHOW_AS_ACTION = "showAsAction";

 	protected Set<Integer> _actionItemIds = new HashSet<Integer>();
 	
	protected ActionBarHelperBase(Activity activity, View.OnClickListener titleListener) {
		super(activity, titleListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		_activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		// ActionBarを初期化する
		_activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar_compat);
		setupActionBar();
		
		// アプリケーションが用意しているオプションメニューをActionBarに設置する
		Menu menu = new ActionBarMenu(_activity);
		_activity.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, menu);
		_activity.onPrepareOptionsMenu(menu);
		for(int i = 0; i < menu.size(); i ++) {
			MenuItem item = menu.getItem(i);
			
			// ただしshowAsActionでActionBar設置を指定しているものに限る
			if(_actionItemIds.contains(item.getItemId())) {
				addActionItemCompatFromMenuItem(item);
			}
		}
	}
	
	private void setupActionBar() {
		final ViewGroup actionBar = getActionBarCompat();
		if(actionBar == null)
			return;
		
		// LAUNCHERボタンをActionBarに設置する
		ActionBarMenu menu = new ActionBarMenu(_activity);
		ActionBarMenuItem item = new ActionBarMenuItem(
											menu,
											android.R.id.home,
											0,
											_activity.getString(R.string.app_name));
		item.setIcon(R.drawable.ic_launcher);
		addActionItemCompatFromMenuItem(item);
		
		// タイトルをActionBarに設置する
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
														0,
														ViewGroup.LayoutParams.FILL_PARENT);
		params.weight = 1;
		TextView titleText = new TextView(_activity, null, R.attr.actionbarCompatTitleStyle);
		titleText.setLayoutParams(params);
		titleText.setText(_activity.getTitle());
		titleText.setOnClickListener(_titleListener);
		actionBar.addView(titleText);
	}
	
	private ViewGroup getActionBarCompat() {
		return (ViewGroup)_activity.findViewById(R.id.actionbar_compat);
	}
	
	private void addActionItemCompatFromMenuItem(final MenuItem item) {
		final ViewGroup actionBar = getActionBarCompat();
		if(actionBar == null)
			return;
		
		// ボタンオブジェクト作成
		final int itemId = item.getItemId();
		ImageButton button = new ImageButton(
										_activity,
										null,
										itemId == android.R.id.home
										? R.attr.actionbarCompatItemHomeStyle
										: R.attr.actionbarCompatItemStyle);
		button.setLayoutParams(
				new ViewGroup.LayoutParams(
					(int)_activity.getResources().getDimension(
														itemId == android.R.id.home
														? R.dimen.actionbar_compat_button_home_width
														: R.dimen.actionbar_compat_button_width),
					ViewGroup.LayoutParams.FILL_PARENT));
		button.setId(itemId);
		button.setImageDrawable(item.getIcon());
		button.setScaleType(ImageView.ScaleType.CENTER);
		button.setContentDescription(item.getTitle());
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				_activity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
			}
		});
		
		// ボタンをActionBarに追加
		actionBar.addView(button);
	}

	@Override
	public boolean onCreateOptionMenu(Menu menu) {
		
		// ActionBarに設置済みのメニューはオプションメニュー上には表示させない
		for(Integer id : _actionItemIds) {
			menu.findItem(id).setVisible(false);
		}
		
		return true;
	}

	@Override
	public void onTitleChanged(CharSequence title, int color) {
		TextView titleView = (TextView) _activity.findViewById(R.id.actionbar_compat_title);
		if (titleView == null)
			return;

		titleView.setText(title);
	}

	@Override
	public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
		return new WrappedMenuInflater(_activity, superMenuInflater);
	}

	public void setEnable() {
		getActionBarCompat().getChildAt(0).setVisibility(View.GONE);
	}
	
	private class WrappedMenuInflater extends MenuInflater {
		private final MenuInflater _inflater;

		public WrappedMenuInflater(Context context, MenuInflater inflater) {
			super(context);
			_inflater = inflater;
		}

		@Override
		public void inflate(int menuRes, Menu menu) {
			loadActionBarMetadata(menuRes);
			_inflater.inflate(menuRes, menu);
		}
		
		private void loadActionBarMetadata(int menuResId) {
			
			// menu.xmlを解析して、showAsAction属性に
			//
			// 1. Always
			// 2. IfRoom
			//
			// が設定されているitem要素のみActionBarに設置するため
			// IDを退避しておく。
			// なお、退避されたIDのメニューはオプションメニューには表示しない。
			
			XmlResourceParser parser = null;
			try {
				parser = _activity.getResources().getXml(menuResId);
				
				int eventType = parser.getEventType();
				int itemId;
				int showAsAction;
				
				boolean eof = false;
				while(!eof) {
					switch(eventType) {
					case XmlPullParser.START_TAG:
						if(!parser.getName().equals("item"))
							break;
						
						itemId = parser.getAttributeResourceValue(
												MENU_RES_NAMESPACE,
												MENU_ATTR_ID,
												0);
						if(itemId == 0)
							break;
						
						showAsAction = parser.getAttributeIntValue(MENU_RES_NAMESPACE, MENU_ATTR_SHOW_AS_ACTION, -1);
						if(	showAsAction == MenuItem.SHOW_AS_ACTION_ALWAYS ||
							showAsAction == MenuItem.SHOW_AS_ACTION_IF_ROOM) {
							_actionItemIds.add(itemId);
						}
						break;
						
					case XmlPullParser.END_DOCUMENT:
						eof = true;
						break;
						
					default:
						break;
					}
					
					eventType = parser.next();
				}
			} catch (XmlPullParserException e) {
				throw new InflateException("Error inflating menu XML", e);
			} catch (IOException e) {
				throw new InflateException("Error inflating menu XML", e);
			} finally {
				if(parser != null) {
					parser.close();
				}
			}
		}
	}

	@Override
	public void setItemVisible(int id, boolean visible) {
		View view = _activity.findViewById(id);
		if(view == null)
			return;
		
		int visibility = View.VISIBLE;
		if(!visible) {
			visibility = View.GONE;
		}
		
		view.setVisibility(visibility);
	}
}
