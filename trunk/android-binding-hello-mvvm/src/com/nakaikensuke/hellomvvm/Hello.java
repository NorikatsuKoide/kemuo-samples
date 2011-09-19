package com.nakaikensuke.hellomvvm;

import gueei.binding.Binder;
import gueei.binding.Command;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Hello extends Activity {
	
	public StringObservable Greeting = new StringObservable("Hello from Android Binding");
	public Command ChangeGreeting = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			Greeting.set("Greeting changed by command");
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Binder.setAndBindContentView(this, R.layout.main, this);
	}
}