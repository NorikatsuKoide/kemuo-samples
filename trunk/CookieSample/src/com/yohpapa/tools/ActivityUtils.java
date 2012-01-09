package com.yohpapa.tools;

/**
Copyright (c) 2011-2012, KENSUKE NAKAI
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list
  of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.
* Neither the name of the nakaikensuke.com nor the names of its contributors may
  be used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
OF SUCH DAMAGE.
*/

import java.io.File;
import java.net.URL;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.widget.Toast;

import com.yohpapa.research.cookiesample.R;

public class ActivityUtils {
	
	public static void startActivity(final Activity activity, File file, String action) {
		Intent intent = IntentBuilder.build(file, Intent.ACTION_VIEW);
		if(intent == null) {
			Toast.makeText(
					activity, R.string.toast_app_not_found, Toast.LENGTH_SHORT).
					show();
			return;
		}
		
		startActivityCommon(activity, intent, action);
	}
	
	public static void startActivity(final Activity activity, URL url, String action) {
		Intent intent = IntentBuilder.build(url, Intent.ACTION_VIEW);
		if(intent == null) {
			Toast.makeText(
					activity, R.string.toast_app_not_found, Toast.LENGTH_SHORT).
					show();
			return;
		}

		startActivityCommon(activity, intent, action);
	}
	
	private static void startActivityCommon(final Activity activity, Intent intent, String action) {
		try {
			activity.startActivity(intent);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(
					activity, R.string.toast_app_not_found, Toast.LENGTH_SHORT).
					show();
		}
	}
}
