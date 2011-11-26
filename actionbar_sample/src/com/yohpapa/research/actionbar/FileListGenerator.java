/*
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
package com.yohpapa.research.actionbar;

import java.io.File;

public class FileListGenerator implements Runnable {
	public interface Callback {
		void notifyFileList(FileListGenerator.FileItem[] files);
	}
	
	public class FileItem {
		private final String _longName;
		private final boolean _isDirectory;
		
		public FileItem(File file) {
			_longName = file.getName();
			_isDirectory = file.isDirectory();
		}
		
		public String getLongName() {return _longName;}
		public boolean isDirectory() {return _isDirectory;}
	}
	
	private final String _path;
	private final Callback _callback;
	public FileListGenerator(String path, Callback callback) {
		_path = path;
		_callback = callback;
	}
	
	@Override
	public void run() {
		FileItem[] items = null;
		try {
			if(_path == null)
				return;
			
			File file = new File(_path);
			if(file.isFile())
				return;
			
			File[] files = file.listFiles();
			items = new FileItem[files.length];
			for(int i = 0; i < files.length; i ++) {
				items[i] = new FileItem(files[i]);
			}
			
		} finally {
			if(_callback != null) {
				_callback.notifyFileList(items);
			}
		}
	}
}
