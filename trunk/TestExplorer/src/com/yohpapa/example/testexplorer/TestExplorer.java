package com.yohpapa.example.testexplorer;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;

/**
 * SDカードエクスプローラアプリリスト画面用Activity
 * @author yohpapa
 *
 */
public class TestExplorer extends Activity {

	private static final String CURRENT_PATH = "CURRENT_PATH";
	
	private TestExplorerViewModel _model = null;
	private String _currentPath = Environment.getExternalStorageDirectory().getPath();
	
	/**
	 * Activity生成イベントハンドラ
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ViewとView Modelをバインド
		_model = new TestExplorerViewModel();
		Binder.setAndBindContentView(this, R.layout.main, _model);
	}

	/**
	 * Activity復帰イベントハンドラ
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// とりあえず場所を初期化する
		_model.setPath(_currentPath);
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
}