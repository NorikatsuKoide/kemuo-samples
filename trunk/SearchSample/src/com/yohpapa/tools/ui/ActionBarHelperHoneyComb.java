package com.yohpapa.tools.ui;

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
	public void onPostCreate(Bundle savedInstanceState) {
		View view = _activity.findViewById(android.R.id.title);
		if(view == null)
			return;
		
		view.setOnClickListener(_titleListener);
	}

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
		final MenuItem item = _menu.findItem(id);
		if(item == null)
			return;
		
		item.setVisible(visible);
	}
}
