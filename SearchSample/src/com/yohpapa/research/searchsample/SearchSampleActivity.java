package com.yohpapa.research.searchsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SearchSampleActivity extends Activity {
	private static final String TAG = SearchSampleActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean result = SearchSampleActivity.this.onSearchRequested();
				Log.d(TAG, "result: " + result);
			}
		});
	}
}