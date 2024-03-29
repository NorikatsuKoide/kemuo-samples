/**
Copyright (c) 2011, KENSUKE NAKAI
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
package com.yohpapa.research.searchsample;

import java.io.File;

public class FileListGenerator implements Runnable {
	public interface Callback {
		void notifyFileList(FileListGenerator.FileItem[] files);
	}
	
	public static class FileItem {

		private final String _longName;
		private final boolean _isDirectory;
		private final String _shortName;
		
		public FileItem(File file) {
			_longName = file.getName();
			_isDirectory = file.isDirectory();
			_shortName = ShortnameHelper.getShortName(file);
		}
		
		public String getLongName() {return _longName;}
		public boolean isDirectory() {return _isDirectory;}
		public String getShortName() {return _shortName;}
	}
	
	private final String _path;
	private final String _query;
	private final Callback _callback;
	public FileListGenerator(String path, String query, Callback callback) {
		_path = path;
		_query = query;
		_callback = callback;
	}
	
	@Override
	public void run() {
		
		// ロケール設定が変えられる可能性があるので
		// 必ずスレッド処理の先頭でセットアップしておくこと
		ShortnameHelper.setup();
		
		// 指定されたパスのエントリを全てリストアップする
		FileItem[] items = new FileItem[0];
		try {
			if(_path == null)
				return;
			
			File file = new File(_path);
			if(file.isFile())
				return;
			
			File[] files = file.listFiles();
			if(files == null)
				return;
			
			items = new FileItem[files.length];
			for(int i = 0; i < files.length; i ++) {
				file = files[i];
				if(_query != null && !_query.equals(file.getName()))
					continue;
					
				items[i] = new FileItem(file);
			}
			
		} finally {
			if(_callback != null) {
				_callback.notifyFileList(items);
			}
		}
	}
	
	private static Thread _thread = null;
	
	public static void start(String path, String query, Callback callback) {
		cancel();
		_thread = new Thread(new FileListGenerator(path, query, callback));
		_thread.start();
	}
	
	public static void cancel() {
		if(_thread == null || !_thread.isAlive())
			return;
		
		_thread.interrupt();
		try {
			_thread.join();
		} catch (InterruptedException e) {}
	}
}
