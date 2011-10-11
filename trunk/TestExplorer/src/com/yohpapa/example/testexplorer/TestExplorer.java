package com.yohpapa.example.testexplorer;

import java.util.Observable;
import java.util.Observer;

import gueei.binding.Binder;
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
	
	private PositionManager _posManager = null;
	
	/**
	 * Activity生成イベントハンドラ
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ViewとView Modelをバインド
		_model = new TestExplorerViewModel(this);
		Binder.setAndBindContentView(this, R.layout.main, _model);
		
		// 表示位置を制御するため泣く泣く取得
		_listView = (ListView)findViewById(R.id.list);
		
		// 表示位置管理用オブジェクト初期化
		_posManager = new PositionManager();
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
		// TODO Auto-generated method stub
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
			
			Integer option = (Integer)param;
			if(option == null)
				return;
			
			int position = 0;
			
			switch(option) {
			case TestExplorerViewModel.CHANGE_DIRECTORY:
				
				// 現在のパスと表示開始位置を保存する
				_currentPath = _model.getCurrentPath();
				position = _listView.getFirstVisiblePosition();
				_posManager.append(_currentPath, position);
				
				break;
				
			case TestExplorerViewModel.ADJUST_POSITION:
				
				// 現在表示パスのラスト位置をキャッシュから引き出して設定する
				_currentPath = _model.getCurrentPath();
				position = _posManager.getPosition(_currentPath);
				_listView.setSelection(position);
				
				break;
				
			default:
				break;
			}
		}
	};
}