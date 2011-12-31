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

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yohpapa.research.searchsample.FileListGenerator.FileItem;
import com.yohpapa.tools.ui.ActionBarActivity;

public class SearchSampleActivity extends ActionBarActivity {
	@SuppressWarnings("unused")
	private static final String TAG = SearchSampleActivity.class.getSimpleName();
	private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();

	private Handler _handler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		_handler = new Handler();
		
		// 端末回転時などは何もしない
		// 全ては生成済みのFragmentに任せる♪
		if(savedInstanceState != null)
			return;
		
		// 起動Intentから表示すべきパスを特定する
		// 受信する可能性のあるIntentは以下の通り
		// 1. ACTION_SEARCH (検索ダイアログ確定)
		// 2. ACTION_VIEW (検索候補選択)
		// 3. ACTION_MAIN (起動)
		Intent intent = getIntent();
		String action = intent.getAction();
		String path;
		if(Intent.ACTION_SEARCH.equals(action)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			path = getPath(intent, query);
		} else if(Intent.ACTION_VIEW.equals(action)) {
			String select = intent.getDataString();
			path = getPath(intent, select);
		} else {
			// 最初はルートから始める
			// ただしタイトルはアプリ名とする
			path = ROOT_PATH;
			setTitle(R.string.app_name);
		}
		
		// もし存在しないパスであれば即終了
		File file = new File(path);
		if(!file.exists()) {
			finish();
			return;
		}
		
		// もしファイルだった場合は暗黙的Intentを投げる
		if(file.isFile()) {
			ActivityUtils.startActivityAndFinish(
					this, file.getAbsolutePath(), Intent.ACTION_VIEW);
			return;
		}
		
		// フォルダだった場合はFragmentを起動して
		// フォルダ内のエントリリストを表示する
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment fragment = FragmentFileList.newInstance(path);
		ft.add(R.id.fragment_filelist, fragment);
		ft.commit();
	}
	
	private String getPath(Intent intent, String fileName) {
		// 受信したIntentからフルパスを抽出する
		Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
		String path = appData.getString(SearchSampleApp.CURRENT_PATH);
		return path + File.separator + fileName;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// アプリ初回起動時のみショートネーム利用不可のダイアログを表示する
		boolean isStarted = PreferenceManager.getAppFirstInvoked(this);
		if(isStarted)
			return;
		
		PreferenceManager.setAppFirstInvoked(this, true);
		
		boolean isAvailable = PreferenceManager.getShortNameAvailable(this);
		if(isAvailable)
			return;
		
		final AlertDialog dialog = new AlertDialog.Builder(this)
											.setCancelable(false)
											.setTitle(R.string.app_name)
											.setMessage(R.string.alert_message)
											.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											})
											.create();
		dialog.show();
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
		case android.R.id.home:
			onOptionMenuHomeSelected();
			break;
			
		case R.id.menu_search:
			onOptionMenuSearchSelected();
			break;
			
		case R.id.menu_setup:
			PreferenceManager.setNameType(this, !PreferenceManager.getNameType(this));
			_observable.notifyObservers(SearchSampleApp.NAME_TYPE_CHANGED);
			break;
			
		case R.id.menu_about:
			onOptionMenuAboutSelected();
			break;
			
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
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

	private void onOptionMenuHomeSelected() {
		
		// 参考サイト
		// http://www.techdoctranslator.com/android/guide/ui/actionbar
		
		Intent intent = new Intent(this, SearchSampleActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void onOptionMenuSearchSelected() {
		String path = getCurrentPath();
		FileListGenerator.start(path, null, new FileListGenerator.Callback() {
			@Override
			public void notifyFileList(final FileItem[] files) {
				_handler.post(new Runnable() {
					@Override
					public void run() {
						// ファイルリストを検索サジェスチョン用コンテンツプロバイダに登録する
						// エントリの数が多いと時間がかかるのが課題・・・
						ContentResolver resolver = getContentResolver();
						resolver.delete(CustomSuggestionsColumns.CONTENT_URI, null, null);
						for(FileItem file : files) {
							ContentValues values = new ContentValues();
							values.put(CustomSuggestionsColumns.TEXT_1, file.getLongName());
							values.put(CustomSuggestionsColumns.TEXT_2, file.getShortName());
							values.put(CustomSuggestionsColumns.INTENT_DATA, file.getLongName());
							resolver.insert(CustomSuggestionsColumns.CONTENT_URI, values);
						}
						
						onSearchRequested();
					}
				});
			}
		});
	}
	
	private String getCurrentPath() {
		// 現在有効な (Foreground) Fragmentの表示パスを取得する
		FragmentFileList fragment = (FragmentFileList)getSupportFragmentManager().findFragmentById(R.id.fragment_filelist);
		return fragment.getCurrentPath();
	}
	
	@Override
	public boolean onSearchRequested() {
		// 現在表示中のパスを引数に設定してから検索ダイアログを起動する
		Bundle appData = new Bundle();
		appData.putString(SearchSampleApp.CURRENT_PATH, getCurrentPath());
		startSearch(null, false, appData, false);
		return true;
	}
	
	public void setActionBarTitle(String title, View.OnClickListener listener) {
		boolean homeVisible = true;
		
		// タイトルが無効な場合はアプリ名とする
		if(title == null) {
			title = getString(R.string.app_name);
			homeVisible = false;
		}
		
		setTitle(title);
		setItemVisibility(android.R.id.home, homeVisible);
		setOnTitleListener(listener);
	}
	
	private void onOptionMenuAboutSelected() {
		
		// アプリのバージョン取得
		String version = "";
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 1).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		// ダイアログのレイアウト生成
		
		// ダイアログ生成
		// TODO: ちゃんと情報を表示すること
		final AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle(getString(R.string.app_name) + " " + version)
			.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}})
			.create();
		
		// ダイアログ表示
		dialog.show();
	}
}