package com.yohpapa.example.testexplorer;

import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.yohpapa.example.tools.MimeUtils;

/**
 * SDカードエクスプローラ用ViewModel
 * @author yohpapa
 *
 */
public class TestExplorerViewModel extends java.util.Observable {
	
	private Activity _hostActivity = null;
	private IListPosition _positioner = null;
	private PositionManager _posManager = null;

	/**
	 * コンストラクタ
	 * @param activity
	 * @param positioner
	 */
	public TestExplorerViewModel(Activity activity, IListPosition positioner) {
		_hostActivity = activity;
		_positioner = positioner;
		_posManager = new PositionManager();
	}
	
	/**
	 * ディレクトリエントリを設定する
	 * @param files
	 */
	public void setPath(String path) {

		// ファイルの場合は移動出来ないので何もしない
		File file = new File(path);
		if(file.isFile()) {
			return;
		}

		// ファイル／ディレクトリが存在しない場合も何もしない
		File[] files = file.listFiles();
		if(files == null || files.length <= 0)
			return;
		
		// 現在の表示ディレクトリのパスを更新する
		CurrentPath.set(path);
		
		// ディレクトリエントリリストを初期化する
		DirectoryEntry[] entries = new DirectoryEntry[files.length];
		for(int i = 0; i < files.length; i ++) {
			DirectoryEntry entry = new DirectoryEntry();
			
			String name = files[i].getName();
			boolean isFile = files[i].isFile();
			entry.Name.set(name);
			entry.IsFile.set(isFile);
			
			entries[i] = entry;
		}
		
		// エントリ列を昇順にソートする
		Arrays.sort(entries, _entryComparator);
		
		// リスト表示を更新する
		DirectoryEntryList.setArray(entries);
	}
	
	// ディレクトリエントリ列ソート用比較オブジェクト
	private final Comparator<DirectoryEntry> _entryComparator = new Comparator<TestExplorerViewModel.DirectoryEntry>() {
		@Override
		public int compare(DirectoryEntry entry1, DirectoryEntry entry2) {
			
			// まずはディレクトリを優先する
			boolean isFile1 = entry1.IsFile.get();
			boolean isFile2 = entry2.IsFile.get();
			if(!isFile1 && isFile2)
				return -1;
			if(isFile1 && !isFile2)
				return 1;
			
			// それでも決着が付かない場合は名前順とする
			return entry1.Name.get().compareTo(entry2.Name.get());
		}
	};
	
	/**
	 * 1階層戻る
	 */
	public boolean backPath() {
		
		// SDカードのTOP階層から上にはイケないことにする
		String path = CurrentPath.get();
		if(path.equals(Environment.getExternalStorageDirectory().getPath()))
			return false;
		
		// あり得ないはずだが、パスが「/」の場合は、上にはイケないことにする
		String[] tmp = path.split("\\/");
		if(tmp.length <= 1)
			return false;
		
		// 文字列を分離して、末尾のみ切り離す
		String dest = "";
		for(int i = 1; i < tmp.length - 1; i ++) {
			dest += "/" + tmp[i];
		}
		
		// 現在の表示位置をクリアする (不要なため)
		_posManager.remove(path);

		// 一つ上の階層に移動する
		setPath(dest);
		
		// 表示位置を通知する
		int position = _posManager.getPosition(dest);
		setChanged();
		notifyObservers(position);

		return true;
	}
	
	/**
	 * 現在表示中の階層のパスを取得する
	 * @return
	 */
	public String getCurrentPath() {
		return CurrentPath.get();
	}
	
	// リストプロパティ
	public ArrayListObservable<DirectoryEntry> DirectoryEntryList =
			new ArrayListObservable<DirectoryEntry>(DirectoryEntry.class);

	// リスト項目プロパティクラス
	public class DirectoryEntry {
		public StringObservable Name = new StringObservable();
		public BooleanObservable IsFile = new BooleanObservable();
	}
	
	// リストクリックプロパティ
	// REMARK!
	// このオブジェクトにクリックされたリストに対応するオブジェクトが設定される
	public Observable<Object> ClickedItem = new Observable<Object>(Object.class);
	
	// 現在パスプロパティ
	public StringObservable CurrentPath = new StringObservable("");
	
	// リストクリックコマンド
	public Command OnItemClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			String org = CurrentPath.get();
			if(org == null)
				return;
			
			DirectoryEntry clicked = (DirectoryEntry)ClickedItem.get();
			if(clicked == null)
				return;
			
			String dest = org + "/" + clicked.Name.get();

			// ファイルをタップしたらIntent起動する
			if(clicked.IsFile.get() == true) {
				sendIntent(dest);
				return;
			}
			
			// 移動する前に現在の位置を保存しておく
			int position = _positioner.getListPosition();
			_posManager.append(org, position);
			
			// 次の階層に移動する
			setPath(dest);
			
			// 新しい階層に移動したら先頭で表示する
			TestExplorerViewModel.this.setChanged();
			TestExplorerViewModel.this.notifyObservers(0);
		}
	};
	
	/**
	 * 指定されたパスのファイルに対する暗黙的Intentを発行する
	 * @param path
	 */
	private void sendIntent(String path) {
		
		// 暗黙Intentのためのパラメータを準備する
		File	file = new File(path);
		Uri		uri  = Uri.fromFile(file);
		String	mime = MimeUtils.getMimeType(file.getName());
		
		// Intentオブジェクトを初期化する
		Intent	intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, mime);
		
		// 暗黙Intentを発行する
		try {
			_hostActivity.startActivity(intent);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(
					_hostActivity,
					_hostActivity.getText(R.string.application_not_found),
					Toast.LENGTH_LONG).show();
		}
	}
	
	// HOMEボタンクリックコマンド
	public Command OnGoToHome = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			
			// 全ての位置をクリアしておく
			_posManager.removeAll();

			// 現在パスをSDカードルートに戻す
			setPath(Environment.getExternalStorageDirectory().getPath());
			
			// HOMEに移動したら先頭で表示する
			TestExplorerViewModel.this.setChanged();
			TestExplorerViewModel.this.notifyObservers(0);
		}
	};
	
	// ×ボタンクリックコマンド
	public Command OnFinish = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			
			// Activityを終了する
			_hostActivity.finish();
		}
	};
}
