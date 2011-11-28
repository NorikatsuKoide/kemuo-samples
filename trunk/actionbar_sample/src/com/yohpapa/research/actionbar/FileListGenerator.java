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
import java.io.UnsupportedEncodingException;

public class FileListGenerator implements Runnable {
	public interface Callback {
		void notifyFileList(FileListGenerator.FileItem[] files);
	}
	
	public static class FileItem {
		static {
			System.loadLibrary("shortname");
		}
		
		private final String _longName;
		private final boolean _isDirectory;
		private final String _shortName;
		
		public FileItem(String path, File file) {
			_longName = file.getName();
			_isDirectory = file.isDirectory();
			byte[] shortName = GetShortName(path, _longName);
			_shortName = encodeShortName(shortName);
		}
		
		public String getLongName() {return _longName;}
		public boolean isDirectory() {return _isDirectory;}
		public String getShortName() {return _shortName;}
		
		native byte[] GetShortName(String dir, String longName);
		
		private String encodeShortName(byte[] shortName) {
			try {
				
				byte[] unicode = new String(shortName, "UTF-8").getBytes("UTF-16BE");
				byte[] cp437 = uni2cp437(unicode);
				return new String(cp437, "Shift_JIS");
				
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		
		private byte[] uni2cp437(byte[] unicode) {
			byte[] destination = new byte[unicode.length / 2];
			
			for(int i = 0, j = 0; i < unicode.length; i += 2) {
				destination[j ++] = Cp437.lookup(unicode[i], unicode[i + 1]);
			}
			
			return destination;
		}
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
				items[i] = new FileItem(_path, files[i]);
			}
			
		} finally {
			if(_callback != null) {
				_callback.notifyFileList(items);
			}
		}
	}
}
