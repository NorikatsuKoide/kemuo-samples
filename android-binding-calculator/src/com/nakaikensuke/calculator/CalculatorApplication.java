package com.nakaikensuke.calculator;

import gueei.binding.Binder;
import android.app.Application;

public class CalculatorApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
	}
}
