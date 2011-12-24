package com.yohpapa.research.searchsample;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

public class IconManager {
	
	private static HashMap<String, Drawable> _caches;
	private static Drawable _defaultIcon;
	
	public static void initialize(Context context) {
		_caches = new HashMap<String, Drawable>();
		_defaultIcon = context.getResources().getDrawable(R.drawable.file_icon);
	}
	
	public static synchronized Drawable getIconFromCache(String fileName) {
		
		// MIME種別が特定できなければデフォルトとする
		String mime = MimeUtils.getMimeType(fileName);
		if(mime == null)
			return _defaultIcon;
		
		// キャッシュにない場合はなかったことを示すnullを返す
		if(!_caches.containsKey(mime))
			return null;
		
		// キャッシュからアイコン取得
		return _caches.get(mime);
	}
	
	public static synchronized void setIconToCache(String mime, Drawable icon) {
		_caches.put(mime, icon);
	}
	
	public static void getIconAsync(Context context, ImageView iconView, String fullPath) {
		IconTask task = new IconTask(iconView, context);
		task.execute(fullPath);
	}
	
	private static class IconTask extends AsyncTask<String, Void, Drawable> {

		private final ImageView _iconView;
		private final int _tag;
		private final Object _lock;
		private final PackageManager _manager;
		
		public IconTask(ImageView iconView, Context context) {
			_iconView = iconView;
			_tag = (Integer)iconView.getTag();
			_manager = context.getPackageManager();
			_lock = context;
		}
		
		@Override
		protected Drawable doInBackground(String... params) {
			
			// ファイル名とMIME種別を取得しておく
			String fullPath = params[0];
			String fileName = new File(fullPath).getName();
			String mimeType = MimeUtils.getMimeType(fileName);
			
			synchronized (_lock) {
				Drawable icon = null;
				try {
					// 何はともあれキャッシュにアイコンがないかチェック
					icon = getIconFromCache(fileName);
					if(icon != null)
						return icon;
					
					// もしなければパッケージマネージャから対応するアプリの一覧を取得
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					Uri uri = Uri.fromFile(new File(fullPath));
					intent.setDataAndType(uri, mimeType);
	
					List<ResolveInfo> list = _manager.queryIntentActivities(
	                                        intent,
	                                        PackageManager.MATCH_DEFAULT_ONLY);
					if(list == null || list.size() <= 0) {
						icon = _defaultIcon;
						return icon;
					}
					
					// 一番目のアプリのアイコンを取得
					icon = list.get(0).loadIcon(_manager);
					return icon;
					
				} finally {
					// 取得したアイコンをキャッシュに登録
					IconManager.setIconToCache(mimeType, icon);
				}
			}
		}

		@Override
		protected void onPostExecute(Drawable result) {
			
			// タグがまだ一致していればアイコンを設定する
			if(_tag != (Integer)_iconView.getTag())
				return;
			
			_iconView.setImageDrawable(result);
		}
	}
}
