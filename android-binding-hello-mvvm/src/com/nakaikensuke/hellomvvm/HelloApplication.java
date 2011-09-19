package com.nakaikensuke.hellomvvm;

import gueei.binding.Binder;
import android.app.Application;

public class HelloApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Load Android Binding in the system.
		Binder.init(this);
	}
}
