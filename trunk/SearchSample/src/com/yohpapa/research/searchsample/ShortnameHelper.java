package com.yohpapa.research.searchsample;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;

/**
 * ショートネームを取得するためのヘルパークラス
 * @author yohpapa
 *
 */
public class ShortnameHelper {
	
	private static boolean _isLoadSuccess = false;
	private static boolean _isShortNameAvailable = false;
	
	static {
		try {
			System.loadLibrary("shortname");
			_isLoadSuccess = true;
		} catch(UnsatisfiedLinkError e) {}
	}
	
	private static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getPath();
	private static final String TEST_FILE_NAME = ShortnameHelper.class.getName();
	
	public static void initialize(Context context) {
		
		// ライブラリのロードが出来なかった場合は問答無用で利用不可とする
		if(!_isLoadSuccess) {
			_isShortNameAvailable = false;
			return;
		}
		
		// アプリ初回起動でない場合、
		// ショートネーム利用可否フラグをロードしておしまい
		boolean isStarted = PreferenceManager.getAppFirstInvoked(context);
		if(isStarted) {
			_isShortNameAvailable = PreferenceManager.getShortNameAvailable(context);
			return;
		}
		
		// アプリ初回起動時はショートネームが利用可能か否かを判定する
		// まずはテストするためのファイルを作成する
		File testFile = new File(EXTERNAL_STORAGE + File.separator + TEST_FILE_NAME);
		if(!testFile.exists()) {
			boolean result = false;
			try {
				result = testFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(!result) {
					_isShortNameAvailable = false;
					return;
				}
			}
		}
		
		// 作成したファイルのショートネームを取得する
		byte[] shortName = GetShortName(EXTERNAL_STORAGE, TEST_FILE_NAME);
		if(shortName != null) {
			_isShortNameAvailable = true;
		}
		
		// 最後にテストファイルを削除する
		testFile.delete();
		
		// ショートネーム利用可否を永続化する
		PreferenceManager.setShortNameAvailable(context, _isShortNameAvailable);
	}
	
	public static boolean isAvailable() {
		return _isLoadSuccess && _isShortNameAvailable;
	}
	
	static String SHORTNAME_ENCODE = "ISO-8859-1";
	public static void setup() {
		
		// ショートネームをデコードするための文字コードを
		// ロケール設定から決定する
		
		Locale locale = Locale.getDefault();
		if(Locale.JAPAN.equals(locale)) {
			SHORTNAME_ENCODE = "Shift_JIS";
		} else if(Locale.KOREA.equals(locale)) {
			//SHORTNAME_ENCODE = "";
		} else if(Locale.SIMPLIFIED_CHINESE.equals(locale)) {
			SHORTNAME_ENCODE = "GBK";
		} else if(Locale.TRADITIONAL_CHINESE.equals(locale)) {
			SHORTNAME_ENCODE = "Big5";
		}
		
		/**
		 * ちなみにJavaで使える文字コードの一覧はこちら
		 * http://java.sun.com/j2se/1.5.0/ja/docs/ja/guide/intl/encoding.doc.html
		 */
	}
	
	public static String getShortName(File file) {
		
		String directoryName = file.getParent();
		String fileName = file.getName();
		
		// ネイティブAPIを使ってショートネーム取得を試みる
		byte[] shortName = GetShortName(directoryName, fileName);
		if(shortName == null)
			return fileName;
		
		// もし取れたらファイル名をUNICODEにデコードする
		return decodeShortName(shortName);
	}
	
	// ショートネーム取得ネイティブAPI
	private native static byte[] GetShortName(String dir, String longName);
	
	private static String decodeShortName(byte[] shortName) {
		try {
			
			// まずは取得したバイト列をUTF-8→UNICODEの順にデコードする
			byte[] unicode = new String(shortName, "UTF-8").getBytes("UTF-16BE");
			
			// その後CP437にデコードする (これがショートネームとして書き込まれた生データ)
			byte[] cp437 = uni2cp437(unicode);
			
			// 最後に適切な文字コードでUNICODEにデコードしておしまい
			return new String(cp437, SHORTNAME_ENCODE);
			
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private static byte[] uni2cp437(byte[] unicode) {
		byte[] destination = new byte[unicode.length / 2];
		
		for(int i = 0, j = 0; i < unicode.length; i += 2) {
			destination[j ++] = Cp437.lookup(unicode[i], unicode[i + 1]);
		}
		
		return destination;
	}
}
