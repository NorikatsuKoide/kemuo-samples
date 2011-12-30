package com.yohpapa.tools.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class ActionBarHelperHoneyComb extends ActionBarHelper {
	private Menu _menu = null;
	
	protected ActionBarHelperHoneyComb(Activity activity, View.OnClickListener titleListener) {
		super(activity, titleListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {}

	@Override
	public boolean onCreateOptionMenu(Menu menu) {
		_menu = menu;
		return true;
	}

	@Override
	public void onTitleChanged(CharSequence title, int color) {}

	@Override
	public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
		return superMenuInflater;
	}

	@Override
	public void setItemVisible(int id, boolean visible) {
		
		// HOMEボタンは専用のメソッドがあるので特別扱いとする
		if(id == android.R.id.home) {
			ActionBar actionBar = _activity.getActionBar();
			if(actionBar == null)
				return;
			
			actionBar.setHomeButtonEnabled(visible);
			actionBar.setDisplayShowHomeEnabled(visible);
			return;
		}
		
		// この方法では本メソッドが呼ばれる前にonOptionMenuCreateが呼ばれていないとダメ
		// ただしそれは最初の画面だけの制約であり、それ以降は問題なし
		if(_menu == null)
			return;
		
		// 指定されたメニューの表示／非表示を切り替える
		final MenuItem item = _menu.findItem(id);
		if(item == null)
			return;
		
		item.setVisible(visible);
	}
}
