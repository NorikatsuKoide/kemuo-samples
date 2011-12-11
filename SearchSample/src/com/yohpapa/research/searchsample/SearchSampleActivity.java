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

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class SearchSampleActivity extends FragmentActivity {
	@SuppressWarnings("unused")
	private static final String TAG = SearchSampleActivity.class.getSimpleName();

	private final ActionBarHelper _helper = new ActionBarHelper(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment fragment = FragmentFileList.newInstance(Environment.getExternalStorageDirectory().getPath());
		ft.add(R.id.fragment_filelist, fragment);
		ft.commit();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		_helper.setup(null);
		_helper.onPostCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_default, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;
		switch(item.getItemId()) {
		case R.id.menu_search:
			result = onSearchRequested();
			break;
			
		case R.id.menu_setup:
			PreferenceManager.setNameType(this, !PreferenceManager.getNameType(this));
			_observable.notifyObservers(SearchSampleApp.NAME_TYPE_CHANGED);
			break;
			
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// ヘルパーが処理したら親にはイベントを渡さない
		boolean result = _helper.onKeyDown(keyCode, event);
		if(result)
			return true;
		
		return super.onKeyDown(keyCode, event);
	}
	
	private final Observable _observable = new Observable() {
		@Override
		public void notifyObservers(Object data) {
			setChanged();
			super.notifyObservers(data);
		}
	};
	
	public void addObserver(Observer observer) {
		_observable.addObserver(observer);
	}
	
	public void deleteObserver(Observer observer) {
		_observable.deleteObserver(observer);
	}

	@Override
	public boolean onSearchRequested() {
		FragmentFileList fragment = (FragmentFileList)getSupportFragmentManager().findFragmentById(R.id.fragment_filelist);
		Bundle appData = new Bundle();
		appData.putString(SearchSampleApp.CURRENT_PATH, fragment.getCurrentPath());
		startSearch(null, false, appData, false);
		return true;
	}
}