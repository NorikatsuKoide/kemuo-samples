package com.nakaikensuke.calculator;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

public class Calculator extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CalculatorViewModel model = new CalculatorViewModel();
		Binder.setAndBindContentView(this, R.layout.main, model);
	}
}