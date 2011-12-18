package com.yohpapa.research.searchsample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.widget.Toast;

public class ActivityUtils {
	
	public static void startActivityAndFinish(final Activity activity, String fullPath, String action) {
		ActivityUtils.startActivity(activity, fullPath, action);
		activity.finish();
	}
	
	public static void startActivity(final Activity activity, String fullPath, String action) {
		Intent intent = IntentBuilder.build(fullPath, Intent.ACTION_VIEW);
		if(intent == null) {
			Toast.makeText(
					activity, R.string.toast_app_not_found, Toast.LENGTH_SHORT).
					show();
			return;
		}
		
		try {
			activity.startActivity(intent);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(
					activity, R.string.toast_app_not_found, Toast.LENGTH_SHORT).
					show();
		}
	}
}
