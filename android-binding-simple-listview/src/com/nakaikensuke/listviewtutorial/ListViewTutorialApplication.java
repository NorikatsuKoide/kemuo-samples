package com.nakaikensuke.listviewtutorial;

import gueei.binding.Binder;
import android.app.Application;

public class ListViewTutorialApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
	}
}
