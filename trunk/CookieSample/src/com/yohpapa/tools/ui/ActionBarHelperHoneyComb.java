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
