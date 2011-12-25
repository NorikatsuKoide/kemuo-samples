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

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBarHelper {

	public static ActionBarHelper createInstance(Activity activity) {
		return new ActionBarHelper(activity);
	}
	
	private Activity _activity = null;
	private ViewGroup _actionBar = null;
	private TextView _titleView = null;
	private View.OnClickListener _titleClickListener = null;
	
	public ActionBarHelper(Activity activity) {
		
		_activity = activity;
		_actionBar = getActionBar();

		// アクションバーのタイトルを初期化
		LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.weight = 1;
		
		_titleView = new TextView(_activity, null, R.attr.actionbarTextStyle);
		_titleView.setLayoutParams(layoutParams);
		_titleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(_titleClickListener == null)
					return;
				
				_titleClickListener.onClick(v);
			}
		});
		
		// タイトルをアクションバーに登録しておく
		_actionBar.addView(_titleView);
	}
	
	public void setup(String title, View.OnClickListener listener) {
		
		// タイトルをアクションバーに設定する
		// nullの場合はアプリ名を表示する
		if(title == null) {
			_titleView.setText(R.string.app_name);
		} else {
			_titleView.setText(title);
		}
		
		// タイトルのクリックリスナを登録しておく
		_titleClickListener = listener;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// MENUはActionBarに統一するため
		// MENUキーは無効とする
		if (keyCode == KeyEvent.KEYCODE_MENU)
			return true;
		
		return false;
	}
	
	public void onPostCreate(Bundle bundle) {
		
		// MenuをActivityに生成してもらう
		Menu menu = new ActionBarMenu(_activity);
		_activity.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, menu);
		
		// ActionBarに追加する
		for(int i = 0; i < menu.size(); i ++) {
			MenuItem item = menu.getItem(i);
			addToActionBar(item);
		}
	}
	
	private void addToActionBar(final MenuItem item) {
		final ViewGroup actionBar = getActionBar();
		if(actionBar == null)
			return;
		
		// セパレータイメージを生成
		ImageView separator = new ImageView(_activity, null, R.attr.actionbarSeparatorStyle);
		separator.setLayoutParams(
				new ViewGroup.LayoutParams(
						getDimensions(R.dimen.actionbar_separator_width),
						ViewGroup.LayoutParams.FILL_PARENT));
		
		// ActionBarボタンを生成
		ImageButton button = new ImageButton(_activity, null, R.attr.actionbarButtonStyle);
		button.setId(item.getItemId());
		button.setLayoutParams(
				new ViewGroup.LayoutParams(
						getDimensions(R.dimen.actionbar_button_width),
						ViewGroup.LayoutParams.FILL_PARENT));
		button.setImageDrawable(item.getIcon());
		button.setScaleType(ImageView.ScaleType.CENTER);
		button.setContentDescription(item.getTitle());
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				_activity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
			}
		});
		
		// セパレータ・ボタンをActionBarに追加
		actionBar.addView(separator);
		actionBar.addView(button);
	}
	
	private ViewGroup getActionBar() {
		return (ViewGroup)_activity.findViewById(R.id.actionbar);
	}
	
	private int getDimensions(int id) {
		Resources res = _activity.getResources();
		if(res == null)
			return 0;
		return res.getDimensionPixelSize(id);
	}
}
