package com.yohpapa.example.testexplorer;

import gueei.binding.Binder;
import android.app.Application;

/**
 * SDカードエクスプローラアプリケーション
 * @author yohpapa
 *
 */
public class TestExplorerApplication extends Application {
	
	/**
	 * アプリケーション生成イベントハンドラ
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Android Bindingを初期化する
		Binder.init(this);
	}
}
