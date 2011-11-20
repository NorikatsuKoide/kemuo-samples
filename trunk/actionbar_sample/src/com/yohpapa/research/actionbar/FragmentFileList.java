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

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

public class FragmentFileList extends ListFragment {

	private Thread _thread = null;
	private Handler _handler = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		_handler = new Handler();
		_thread = new Thread(
						new FileListGenerator(
							Environment.getExternalStorageDirectory().toString(),
							_fileListCallback));
		_thread.start();
	}
	
	private final FileListGenerator.Callback _fileListCallback = new FileListGenerator.Callback() {
		@Override
		public void notifyFileList(final File[] files) {
			if(files == null || files.length <= 0)
				return;
			
			_handler.post(new Runnable() {
				@Override
				public void run() {
					setListAdapter(
							new ArrayAdapter<String>(
									getActivity(),
									android.R.layout.simple_list_item_1,
									getFileNameList(files)));
				}
			});
		}
	};
	
	private String[] getFileNameList(File[] files) {
		String[] nameList = new String[files.length];
		
		for(int i = 0; i < files.length; i ++) {
			nameList[i] = files[i].getName();
		}
		
		return nameList;
	}
}
