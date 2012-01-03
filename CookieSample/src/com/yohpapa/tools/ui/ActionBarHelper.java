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

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public abstract class ActionBarHelper {
	protected Activity _activity = null;
	protected View.OnClickListener _titleListener = null;
	
	public static ActionBarHelper createInstance(Activity activity, View.OnClickListener titleListener) {
		// APIレベルに合わせてActionBarHelperオブジェクトを生成する
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return new ActionBarHelperICS(activity, titleListener);
		} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return new ActionBarHelperHoneyComb(activity, titleListener);
		} else {
			return new ActionBarHelperBase(activity, titleListener);
		}
	}
	
	protected ActionBarHelper(Activity activity, View.OnClickListener titleListener) {
		_activity = activity;
		_titleListener = titleListener;
	}
	
	// 以下は各APIレベルに対応したサブクラスでオーバーライドするメソッド
	public abstract void onCreate(Bundle savedInstanceState);
	public abstract void onPostCreate(Bundle savedInstanceState);
	public abstract boolean onCreateOptionMenu(Menu menu);
	public abstract void onTitleChanged(CharSequence title, int color);
	public abstract MenuInflater getMenuInflater(MenuInflater superMenuInflater);
	public abstract void setItemVisible(int id, boolean visible);
}
