package com.yohpapa.example.testexplorer;

import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;

import android.os.Environment;
import android.view.View;

/**
 * SDカードエクスプローラ用ViewModel
 * @author yohpapa
 *
 */
public class TestExplorerViewModel {
	
	public ArrayListObservable<DirectoryEntry> DirectoryEntryList =
					new ArrayListObservable<DirectoryEntry>(DirectoryEntry.class);
	
	public class DirectoryEntry {
		public StringObservable Name = new StringObservable();
		public BooleanObservable IsFile = new BooleanObservable();
	}

	private String _currentPath = null;
	
	/**
	 * ディレクトリエントリを設定する
	 * @param files
	 */
	public void setPath(String path) {

		File file = new File(path);
		if(file.isFile()) {
			return;
		}

		File[] files = file.listFiles();
		if(files.length <= 0)
			return;
		
		_currentPath = path;
		
		DirectoryEntry[] entries = new DirectoryEntry[files.length];
		for(int i = 0; i < files.length; i ++) {
			DirectoryEntry entry = new DirectoryEntry();
			
			String name = files[i].getName();
			boolean isFile = files[i].isFile();
			entry.Name.set(name);
			entry.IsFile.set(isFile);
			
			entries[i] = entry;
		}
		
		DirectoryEntryList.setArray(entries);
	}
	
	/**
	 * 1階層戻る
	 */
	public boolean backPath() {
		
		// SDカードのTOP階層から上にはイケないことにする
		if(_currentPath.equals(Environment.getExternalStorageDirectory().getPath()))
			return false;
		
		// あり得ないはずだが、パスが「/」の場合は、上にはイケないことにする
		String[] tmp = _currentPath.split("\\/");
		if(tmp.length <= 1)
			return false;
		
		// 文字列を分離して、末尾のみ切り離す
		String dest = "";
		for(int i = 1; i < tmp.length - 1; i ++) {
			dest += "/" + tmp[i];
		}
		
		// 一つ上の階層に移動する
		setPath(dest);
		return true;
	}
	
	// リストクリックイベントハンドラオブジェクト
	public Command OnItemClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(_currentPath == null)
				return;
			
			DirectoryEntry clicked = (DirectoryEntry)ClickedItem.get();
			if(clicked == null)
				return;
			
			if(clicked.IsFile.get() == true)
				return;
			
			String path = _currentPath + "/" + clicked.Name.get();
			setPath(path);
		}
	};
	
	// リストクリックオブジェクト
	// REMARK!
	// このオブジェクトにクリックされたリストに対応するオブジェクトが設定される
	public Observable<Object> ClickedItem = new Observable<Object>(Object.class);
}
