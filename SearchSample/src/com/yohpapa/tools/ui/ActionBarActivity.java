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
package com.yohpapa.tools.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class ActionBarActivity extends FragmentActivity {
	private final View.OnClickListener _titleListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if(_userTitleListener == null)
				return;
			
			_userTitleListener.onClick(view);
		}
	};
	private View.OnClickListener _userTitleListener = null;
	final ActionBarHelper _actionBarHelper = ActionBarHelper.createInstance(this, _titleListener);
	
	protected ActionBarHelper getActionBarHelper() {
		return _actionBarHelper;
	}

	@Override
	public MenuInflater getMenuInflater() {
		return _actionBarHelper.getMenuInflater(super.getMenuInflater());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_actionBarHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		_actionBarHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = false;
		result |= super.onCreateOptionsMenu(menu);
		result |= _actionBarHelper.onCreateOptionMenu(menu);
		return result;
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		_actionBarHelper.onTitleChanged(title, color);
		super.onTitleChanged(title, color);
	}
	
	public void setItemVisibility(int id, boolean visible) {
		_actionBarHelper.setItemVisible(id, visible);
	}
	
	public void setOnTitleListener(View.OnClickListener titleListener) {
		_userTitleListener = titleListener;
	}
}
