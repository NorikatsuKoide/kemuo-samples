package com.yohpapa.example.testexplorer;

import gueei.binding.Binder;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.ListView;

/**
 * SDカードエクスプローラアプリリスト画面用Activity
 * @author yohpapa
 *
 */
public class TestExplorer extends Activity {

	private static final String CURRENT_PATH = "CURRENT_PATH";
	
	private TestExplorerViewModel _model = null;
	private String _currentPath = Environment.getExternalStorageDirectory().getPath();
	
	private ListView _listView = null;
	
	/**
	 * Activity生成イベントハンドラ
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ViewとView Modelをバインド
		_model = new TestExplorerViewModel(this, _listPosition);
		Binder.setAndBindContentView(this, R.layout.main, _model);
		
		// 表示位置を制御するため泣く泣く取得
		_listView = (ListView)findViewById(R.id.list);
	}

	/**
	 * Activity復帰イベントハンドラ
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// 表示階層変更監視
		_model.addObserver(_observer);

		// とりあえず場所を初期化する
		_model.setPath(_currentPath);
	}
	
	/**
	 * Activity一時停止イベントハンドラ
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		// 表示階層変更監視解除
		_model.deleteObserver(_observer);
	}

	/**
	 * Activity状態復帰イベントハンドラ
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		_currentPath = savedInstanceState.getString(CURRENT_PATH);
	}

	/**
	 * Activity状態保存イベントハンドラ
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		_currentPath = _model.getCurrentPath();
		outState.putString(CURRENT_PATH, _currentPath);
	}

	/**
	 * キーイベントハンドラ
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			boolean result = _model.backPath();
			if(result)
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// 表示階層変更監視オブジェクト
	private final Observer _observer = new Observer() {
		@Override
		public void update(Observable observable, Object param) {
			
			Integer position = (Integer)param;
			if(position == null)
				return;
			
			if(_listView == null)
				return;
			
			_listView.setSelection(position);
		}
	};
	
	// リスト位置取得インターフェイスオブジェクト
	private final IListPosition _listPosition = new IListPosition() {
		@Override
		public int getListPosition() {
			if(_listView == null)
				return 0;
			
			return _listView.getFirstVisiblePosition();
		}
	};
}